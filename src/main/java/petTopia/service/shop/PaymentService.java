package petTopia.service.shop;

import java.math.BigDecimal;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpEntity;

import petTopia.dto.shop.PaymentInfoDto;
import petTopia.dto.shop.PaymentResponseDto;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Order;
import petTopia.model.shop.Payment;
import petTopia.model.shop.PaymentCategory;
import petTopia.model.shop.PaymentStatus;
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.PaymentRepository;
import petTopia.repository.shop.PaymentStatusRepository;
import petTopia.util.EcpayUtils; // 引入 EcpayUtils

@Service
public class PaymentService {

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

    public PaymentResponseDto sendECPayPaymentRequest(Order order) throws Exception {
        RestTemplate restTemplate = new RestTemplate();

        // 設定 ECPay API 參數
        String url = paymentUrl;  // 這個變數要對應你的 ECPay 付款 API
        String orderResultUrl = "https://localhost:5173/orders/" + order.getId();

        // 找到該 member 的購物車資訊
        List<Cart> cartItems = cartService.getCartItems(order.getMember().getId());

        // 合併所有商品名稱
        StringBuilder itemNameBuilder = new StringBuilder();
        for (Cart cartItem : cartItems) {
            // 假設 Cart 物件有一個方法 getProductName() 來取得商品名稱
            if (itemNameBuilder.length() > 0) {
                itemNameBuilder.append("#");
            }
            itemNameBuilder.append(cartItem.getProduct().getProductDetail().getName());
        }

        // 生成商品名稱
        String itemName = itemNameBuilder.toString();
        String paymentAmount = order.getTotalAmount().toString();

        // 使用 MultiValueMap 來封裝參數
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("MerchantID", merchantId);
        params.add("MerchantTradeNo", order.getId().toString());  // 使用 order 的 ID 作為 MerchantTradeNo
        params.add("MerchantTradeDate", new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
        params.add("PaymentType", "aio");
        params.add("TotalAmount", paymentAmount);
        params.add("TradeDesc", "PetTopia商品付款");
        params.add("ItemName", itemName);
        params.add("ReturnURL", "http://localhost:8080/shop/payment/ecpay/callback");  // 這裡是 callback URL
        params.add("OrderResultURL", orderResultUrl);
        params.add("ChoosePayment", "Credit");
        params.add("EncryptType", "1");

     // 轉換 MultiValueMap 為 Map<String, String>，選擇第一個值
        Map<String, String> singleValueMap = new LinkedHashMap<>();
        for (Map.Entry<String, List<String>> entry : params.entrySet()) {
            singleValueMap.put(entry.getKey(), entry.getValue().get(0)); // 取每個鍵的第一個值
        }

        // 計算 CheckValue
        String checkValue = ecpayUtils.createCheckValue(singleValueMap);  // 使用 Map<String, String>

        // 把 CheckValue 加入到 params 中
        params.add("CheckValue", checkValue);
        // 設定 headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);  // 設定 Content-Type

        // 設置 HTTP 實體
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        // 發送請求
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        // 檢查回應狀態碼
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new Exception("ECPay API returned an error. Status code: " + response.getStatusCode());
        }

        String responseBody = response.getBody();
        System.out.println(responseBody);
        if (responseBody != null && responseBody.startsWith("<")) {
            Document document = Jsoup.parse(responseBody);
            Element formElement = document.select("form").first();
            if (formElement != null) {
                String actionUrl = formElement.attr("action");

                if (actionUrl != null && !actionUrl.isEmpty()) {
                    return new PaymentResponseDto(true, new BigDecimal(paymentAmount), actionUrl); // 回傳 actionUrl
                } else {
                    throw new Exception("Unable to extract payment URL from ECPay response.");
                }
            } else {
                throw new Exception("ECPay response does not contain a valid form.");
            }
        } else {
            throw new Exception("Unexpected response format from ECPay: " + responseBody);
        }
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
