package petTopia.service.shop;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.PaymentRepository;
import petTopia.repository.shop.PaymentStatusRepository;
import petTopia.util.EcpayUtils; // 引入 EcpayUtils

@Service
public class PaymentService {
	private static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    @Value("${ecpay.merchantId}")
    private String merchantId;

    @Value("${ecpay.paymentUrl}")
    private String paymentUrl;

    @Autowired
    private CartService cartService;
    
    @Autowired
    private PaymentRepository paymentRepo;
    
    @Autowired
    private PaymentStatusRepository paymentStatusRepo;

    @Autowired
    private PaymentCategoryRepository paymentCategoryRepo;
    
    @Autowired
    private OrderDetailRepository orderDetailRepo;
    
    @Autowired
    private EcpayUtils ecpayUtils; // 引入 EcpayUtils

    public PaymentInfoDto getPaymentInfoDto(Order order) {
        PaymentInfoDto paymentInfoDto = new PaymentInfoDto();
        paymentInfoDto.setPaymentAmount(order.getPayment().getPaymentAmount());
        paymentInfoDto.setPaymentCategory(order.getPayment().getPaymentCategory().getName());
        paymentInfoDto.setPaymentStatus(order.getPayment().getPaymentStatus().getName());
        return paymentInfoDto;
    }

    // 處理信用卡支付
    public String processCreditCardPayment(Order order, Integer paymentCategoryId) throws Exception {
        
    	 log.info("開始處理信用卡付款, 訂單編號: {}", order.getId());
    	// 根據 paymentCategoryId 查詢對應的 PaymentCategory
        PaymentCategory paymentCategory = paymentCategoryRepo.findById(paymentCategoryId)
                .orElseThrow(() -> new Exception("Invalid PaymentCategoryId")); // 如果沒有找到，丟出異常

        BigDecimal orderTotal = order.getTotalAmount();

        // 呼叫 ECPay 生成支付請求，獲取付款網址
        PaymentResponseDto paymentResponse = sendECPayPaymentRequest(order);

        Payment payment = new Payment();
        payment.setOrder(order);

        if (paymentResponse.isSuccess()) {
            payment.setPaymentAmount(paymentResponse.getPaymentAmount());
        } else {
            payment.setPaymentAmount(BigDecimal.ZERO);
        }

        payment.setPaymentCategory(paymentCategory);
        payment.setPaymentDate(new Date());
        payment.setUpdatedDate(new Date());

        if (paymentResponse.isSuccess()) {
            setPaymentStatus(payment, 2);  // 設置為 "已付款"
        } else {
            setPaymentStatus(payment, 3);  // 設置為 "付款失敗"
        }

        paymentRepo.save(payment);

        return paymentResponse.getPaymentUrl(); // **回傳 ECPay 付款網址**
    }

    @Transactional
    public PaymentResponseDto sendECPayPaymentRequest(Order order) throws Exception {

        // 從訂單中獲取訂單明細 (OrderItem)
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

        // 建立 ECPay 參數 Map
        Map<String, String> params = new LinkedHashMap<>();
        params.put("MerchantID", merchantId);
        params.put("MerchantTradeNo", "PetTopia" + order.getId());
        params.put("MerchantTradeDate", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        params.put("PaymentType", "aio");
        params.put("TotalAmount", paymentAmount);
        params.put("TradeDesc", URLEncoder.encode("petTopia商品付款", StandardCharsets.UTF_8.toString()));
        params.put("ItemName", URLEncoder.encode(itemName, StandardCharsets.UTF_8.toString()));

        params.put("ReturnURL", "http://localhost:8080/shop/payment/ecpay/callback");
        params.put("OrderResultURL","http://localhost:5173/shop/orders/" + order.getId() );
        params.put("ChoosePayment", "Credit");
        params.put("EncryptType", "1");

        System.out.println("印出itemname:"+itemName);
        // 計算 CheckValue
        String checkValue = ecpayUtils.createCheckValue(params);
        params.put("CheckMacValue", checkValue);
        
     // 產生 POST 表單 HTML
        StringBuilder scriptHtml = new StringBuilder();
        scriptHtml.append("<script>");
        scriptHtml.append("var form = document.createElement('form');");
        scriptHtml.append("form.method = 'POST';");
        scriptHtml.append("form.action = '").append(paymentUrl).append("';");

        System.out.println(scriptHtml.toString());

        // 在這裡，將所有必要的參數添加到表單中
        for (Map.Entry<String, String> entry : params.entrySet()) {
            scriptHtml.append("var input = document.createElement('input');");
            scriptHtml.append("input.type = 'hidden';");
            scriptHtml.append("input.name = '").append(entry.getKey()).append("';");
            scriptHtml.append("input.value = '").append(entry.getValue()).append("';");
            scriptHtml.append("form.appendChild(input);");
        }

        scriptHtml.append("document.body.appendChild(form);");
        scriptHtml.append("form.submit();");
        scriptHtml.append("</script>");

        // 返回 HTML 和跳轉指令
        return new PaymentResponseDto(true, new BigDecimal(paymentAmount), scriptHtml.toString(), checkValue);
}


    // **解析 ECPay API 回應，提取付款網址**
    private String extractPaymentUrl(String ecpayResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(ecpayResponse);
            if (jsonNode.has("PaymentURL")) {
                return jsonNode.get("PaymentURL").asText(); // **從 JSON 取出付款網址**
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

 // 處理 ECPay 回調，更新支付狀態
    public void handleEcpayCallback(Map<String, String> callbackParams) throws Exception {
        // 確認回傳的 CheckValue 是否有效，防止偽造回調
        String checkValue = callbackParams.get("CheckValue");
        if (!ecpayUtils.isValidCheckValue(callbackParams, checkValue)) {
            throw new IllegalArgumentException("Invalid CheckValue from ECPay callback");
        }

        // 取得回傳資料
        String merchantTradeNo = callbackParams.get("MerchantTradeNo");  // 這是你的訂單編號（MerchantTradeNo）
        String paymentStatus = callbackParams.get("PaymentStatus");  // 支付狀態
        String tradeNo = callbackParams.get("TradeNo");  // 這是 ECPay 返回的交易編號

        // 將 merchantTradeNo 從 String 轉換為 Integer
        Integer orderId = Integer.valueOf(merchantTradeNo);  // 注意這裡的轉換

        // 根據 orderId 查找對應的支付記錄
        Optional<Payment> paymentOptional = Optional.of(paymentRepo.findByOrderId(orderId)); // 這裡的 orderId 是 Integer 類型
        if (!paymentOptional.isPresent()) {
            throw new IllegalArgumentException("Payment record not found for order: " + merchantTradeNo);
        }

        Payment payment = paymentOptional.get();

        // 根據回傳的支付狀態來更新訂單的支付狀態
        if ("1".equals(paymentStatus)) { // 付款成功
            setPaymentStatus(payment, 2); // 設為已付款
        } else {
            setPaymentStatus(payment, 3); // 設為付款失敗
        }

        // 更新支付交易號等信息
        payment.setTradeNo(tradeNo);  // 保存 ECPay 返回的交易號
        payment.setUpdatedDate(new Date());
        paymentRepo.save(payment);
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
