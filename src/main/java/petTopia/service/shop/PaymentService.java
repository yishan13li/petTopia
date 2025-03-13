package petTopia.service.shop;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;

import petTopia.dto.shop.PaymentInfoDto;
import petTopia.dto.shop.PaymentResponseDto;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Order;
import petTopia.model.shop.OrderDetail;
import petTopia.model.shop.Payment;
import petTopia.model.shop.PaymentCategory;
import petTopia.model.shop.PaymentStatus;
import petTopia.repository.shop.OrderDetailRepository;
import petTopia.repository.shop.OrderRepository;
import petTopia.repository.shop.OrderStatusRepository;
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.PaymentRepository;
import petTopia.repository.shop.PaymentStatusRepository;
import petTopia.util.EcpayUtils; // 引入 EcpayUtils

@Service
public class PaymentService {
	private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Value("${ecpay.merchantId}")
    private String merchantId;

    @Autowired
    private CartService cartService;
    
    @Autowired
    private PaymentRepository paymentRepo;
    
    @Autowired
    private PaymentStatusRepository paymentStatusRepo;

    @Autowired
    private PaymentCategoryRepository paymentCategoryRepo;
    
    @Autowired
    private OrderRepository orderRepo;
    
    @Autowired
    private OrderDetailRepository orderDetailRepo;
    
    @Autowired
    private OrderStatusRepository orderStatusRepo;
    
    @Autowired
    private EcpayUtils ecpayUtils; // 引入 EcpayUtils

    public PaymentInfoDto getPaymentInfoDto(Order order) {
        PaymentInfoDto paymentInfoDto = new PaymentInfoDto();
        paymentInfoDto.setPaymentAmount(order.getPayment().getPaymentAmount());
        paymentInfoDto.setPaymentCategory(order.getPayment().getPaymentCategory().getName());
        paymentInfoDto.setPaymentStatus(order.getPayment().getPaymentStatus().getName());
        return paymentInfoDto;
    }
    
    //訂單建立後為待處理(1)，先從訂單資訊取得ECpay需要的參數
    @Transactional
    public PaymentResponseDto processCreditCardPayment(Order order, Integer paymentCategoryId) throws Exception {
        log.info("開始處理信用卡付款, 訂單編號: {}", order.getId());

        // 檢查是否為 paymentCategoryId == 1
        if (paymentCategoryId != 1) {
            throw new IllegalArgumentException("只有信用卡付款才可執行該方法");
        }

        // 根據訂單資訊產生ECpay需要的參數
        List<OrderDetail> orderDetails = orderDetailRepo.findByOrderId(order.getId());

        // 合併所有商品名稱
        StringBuilder itemNameBuilder = new StringBuilder();
        for (OrderDetail orderDetail : orderDetails) {
            if (itemNameBuilder.length() > 0) {
                itemNameBuilder.append("#");
            }
            itemNameBuilder.append(orderDetail.getProduct().getProductDetail().getName());
        }

        String itemName = itemNameBuilder.toString();
        String paymentAmount = order.getTotalAmount().toString();

        // 創建 PaymentResponseDto 並設置值
        PaymentResponseDto paymentResponse = new PaymentResponseDto();
        paymentResponse.setMerchantId(merchantId);
        paymentResponse.setMerchantTradeNo("PetTopia" + order.getId());
        paymentResponse.setMerchantTradeDate(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        paymentResponse.setPaymentType("aio");
        paymentResponse.setTotalAmount(paymentAmount);
        paymentResponse.setTradeDesc("petTopia商品付款");
        paymentResponse.setItemName(itemName);
        paymentResponse.setReturnURL("http://localhost:8080/shop/payment/ecpay/callback");
//        paymentResponse.setOrderResultURL("http://localhost:5173/shop/orders/" + order.getId());
        paymentResponse.setChoosePayment("Credit");
        paymentResponse.setEncryptType("1");

        // 計算 CheckValue
        String checkValue = ecpayUtils.createCheckValue(paymentResponse.toMap());
        paymentResponse.setCheckMacValue(checkValue);

        // 返回支付參數資料
        return paymentResponse;
    }

    //告訴ECpay我有收到他回傳的狀態資料，並且核對檢查碼
    public Map<String, String> returnStatusToEcpay(Map<String, String> callbackParams) throws Exception {
        
        Map<String, String> response = new HashMap<>();

        // 取得 ECPay 回傳的交易狀態 (RtnCode)
        String rtnCode = callbackParams.get("RtnCode");

        // 根據RtnCode(支付狀態)來設定回應的 RtnCode 和 RtnMsg
        if ("1".equals(rtnCode)) {  // 付款成功
            response.put("RtnCode", "1");
            response.put("RtnMsg", "OK");
        } else {  // 付款失敗或其他情況
            response.put("RtnCode", "0");
            response.put("RtnMsg", "Error: Payment Failed");
        }
        
        // 確認ECpay回傳的 CheckMacValue 是否有效，防止偽造回調
        boolean isValid = ecpayUtils.isValidCheckValue(callbackParams);

        // 根據檢查碼的驗證結果來決定是否需要更改回應
        if (!isValid) {
            response.put("RtnCode", "0");  // 重新設置為錯誤狀態
            response.put("RtnMsg", "Error: Invalid CheckMacValue");
        }

        return response;
    }

    // 處理 ECPay 回調，更新支付狀態
    public String handleEcpayCallback(Map<String, String> callbackParams) throws Exception {
        // 取得回傳資料
        String merchantTradeNo = callbackParams.get("MerchantTradeNo");  // 這是你的訂單編號（MerchantTradeNo）
        String rtnCode = callbackParams.get("RtnCode");  // 支付狀態
        String tradeNo = callbackParams.get("TradeNo");  // 這是 ECPay 返回的交易編號
        String tradeAmt = callbackParams.get("TradeAmt");  // 這是 ECPay 返回的交易金額
        String paymentDateStr = callbackParams.get("PaymentDate");  // 這是 ECPay 返回的支付日期

        // 去除 "PetTopia" 前綴，僅保留訂單編號部分
        String orderIdStr = merchantTradeNo.replace("PetTopia", "");

        // 將 orderIdStr 轉換為 Integer
        Integer orderId = Integer.valueOf(orderIdStr);
        BigDecimal paymentAmount = new BigDecimal(tradeAmt);

        // 根據 orderId查找對應的訂單
        Optional<Order> orderOptional = orderRepo.findById(orderId);
        if (!orderOptional.isPresent()) {
            throw new IllegalArgumentException("Order not found for merchantTradeNo: " + merchantTradeNo);
        }

        Order order = orderOptional.get();
        Payment payment = new Payment();
        payment.setOrder(order);

        // 確認回傳的 CheckMacValue 是否有效，防止偽造回調
        if (!ecpayUtils.isValidCheckValue(callbackParams) || "0".equals(rtnCode)) {
            // 若 CheckMacValue 無效或者付款狀態為失敗（RtnCode為0），則設置為付款失敗
            setPaymentStatus(payment, 3); // 設為付款失敗
            payment.setPaymentAmount(null);
        } else if ("1".equals(rtnCode)) {
            // 若 RtnCode為1，表示付款成功，設置為已付款
            setPaymentStatus(payment, 2); // 設為已付款
            payment.setPaymentAmount(paymentAmount);

            // 更新訂單狀態為待出貨（2）
            order.setOrderStatus(orderStatusRepo.findById(2)
                .orElseThrow(() -> new IllegalArgumentException("找不到待出貨狀態")));
            orderRepo.save(order);  // 更新訂單
        }

        // 確認 PaymentCategory 並設置
        paymentCategoryRepo.findById(1).ifPresentOrElse(
            paymentCategory -> payment.setPaymentCategory(paymentCategory),
            () -> { throw new IllegalArgumentException("PaymentCategory not found"); }
        );

        // 定義日期格式，根據回傳的格式來設定
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        // 解析字符串，轉換成 Date
        Date paymentDate = null;
        if (paymentDateStr != null && !paymentDateStr.isEmpty()) {
            try {
                paymentDate = dateFormat.parse(paymentDateStr);  // 解析成 Date 物件
            } catch (ParseException e) {
                e.printStackTrace();
                throw new IllegalArgumentException("Invalid payment date format: " + paymentDateStr, e);
            }
        } else {
            throw new IllegalArgumentException("Payment date is missing");
        }

        // 更新支付交易號等信息
        payment.setTradeNo(tradeNo);  // 保存 ECPay 返回的交易號
        payment.setPaymentDate(paymentDate);  // 設置為 Payment 的 paymentDate 欄位
        payment.setUpdatedDate(new Date());  // 設置為更新日期
        paymentRepo.save(payment);  // 保存到資料庫

        return "OK";  // 回傳 "OK" 以符合 ECPay 的需求
    }

    // 處理貨到付款支付
    public boolean createCashOnDeliveryPayment(Order order, PaymentCategory paymentCategory) {
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentCategory(paymentCategory);
        payment.setPaymentDate(new Date());
        payment.setUpdatedDate(new Date());

        // 設定支付狀態為待處理
        setPaymentStatus(payment, 1); // 設置為 "待付款"（ID = 1）

        try {
            paymentRepo.save(payment);
            return true;  // 如果成功保存，回傳 true
        } catch (Exception e) {
            return false; // 如果發生錯誤，回傳 false
        }
    }

    // 將設置支付狀態的邏輯提取成一個方法
    private void setPaymentStatus(Payment payment, int statusId) {
        Optional<PaymentStatus> paymentStatus = paymentStatusRepo.findById(statusId);
        paymentStatus.ifPresent(payment::setPaymentStatus); // 只有當 paymentStatus 存在時才設置
    }
}
