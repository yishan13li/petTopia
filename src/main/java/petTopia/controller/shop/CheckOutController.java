package petTopia.controller.shop;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;
import petTopia.dto.shop.OrderSummaryAmoutDto;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Coupon;
import petTopia.model.shop.Order;
import petTopia.model.shop.Payment;
import petTopia.model.shop.PaymentCategory;
import petTopia.model.shop.PaymentStatus;
import petTopia.model.shop.ShippingAddress;
import petTopia.model.shop.ShippingCategory;
import petTopia.model.user.Member;
import petTopia.service.shop.CartService;
import petTopia.service.shop.CouponService;
import petTopia.service.shop.OrderService;
import petTopia.service.shop.PaymentService;
import petTopia.util.EcpayUtils;
import petTopia.repository.shop.OrderRepository;
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.PaymentRepository;
import petTopia.repository.shop.PaymentStatusRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
import petTopia.repository.shop.ShippingAddressRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/shop")
public class CheckOutController {

	@Autowired
	private EcpayUtils ecpayUtils;

    @Autowired
    private CartService cartService;

    @Autowired
    private ShippingAddressRepository shippingAddressRepo;

    @Autowired
    private ShippingCategoryRepository shippingCategoryRepo;

    @Autowired
    private CouponService couponService;

    @Autowired
    private PaymentCategoryRepository paymentCategoryRepo;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private PaymentRepository paymentRepo;
    
    @Autowired
    private PaymentStatusRepository paymentStatusRepo;
    
    @Autowired
    private OrderRepository orderRepo;

    @GetMapping("/checkout")
    public ResponseEntity<Object> getCheckoutInfo(HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        if (member == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "請先登入"));
        }
        
        Integer memberId = member.getId();
        List<Cart> cartItems = cartService.getCartItems(memberId);
        BigDecimal subtotal = cartService.calculateTotalPrice(cartItems);
        List<ShippingCategory> shippingCategories = shippingCategoryRepo.findAll();
        List<PaymentCategory> paymentCategories = paymentCategoryRepo.findAll();
        
        Map<String, Object> response = new HashMap<>();
        response.put("member", member);
        response.put("cartItems", cartItems.isEmpty() ? Collections.emptyList() : cartItems);
        response.put("subtotal", subtotal);
        response.put("shippingCategories", shippingCategories);
        response.put("paymentCategories", paymentCategories);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member")
    public ResponseEntity<Object> getMemberInfo(HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        
        // 如果會員資訊不存在，返回錯誤訊息
        if (member == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                 .body(Map.of("message", "未找到會員資料"));
        }

        // 若會員資料存在，返回該會員資訊
        return ResponseEntity.ok(member);
    }
    
    @GetMapping("/shipping/address")
    public ResponseEntity<Object> getShippingAddress(HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        ShippingAddress lastShippingAddress = shippingAddressRepo.findByMemberAndIsCurrent(member, true);
        if (lastShippingAddress == null) {
            lastShippingAddress = new ShippingAddress();  // 避免前端渲染錯誤
        }
        return ResponseEntity.ok(lastShippingAddress);
    }
    
    @GetMapping("/coupons")
    public ResponseEntity<Object> getCoupons(HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        Integer memberId = member.getId();
        List<Cart> cartItems = cartService.getCartItems(memberId);
        BigDecimal subtotal = cartService.calculateTotalPrice(cartItems);

        // 更新優惠券使用次數
        couponService.updateCouponUsageCount(memberId);
        Map<String, List<Coupon>> couponsMap = couponService.getCouponsByAmount(memberId, subtotal);

        List<Coupon> availableCoupons = couponsMap.get("available");
        List<Coupon> notMeetCoupons = couponsMap.get("notMeet");

        return ResponseEntity.ok(Map.of(
            "availableCoupons", availableCoupons,
            "notMeetCoupons", notMeetCoupons
        ));
    }
    


    @PostMapping("/checkout")
    public ResponseEntity<?> processCheckout(@RequestBody Map<String, Object> checkoutData, 
                                              HttpSession session,
                                              @RequestHeader(value = "Accept", defaultValue = "application/json") String acceptHeader) throws Exception {

        Member member = (Member) session.getAttribute("member");
        Integer memberId = member.getId();

        Integer couponId = checkoutData.get("couponId") != null ? (Integer) checkoutData.get("couponId") : null;
        Integer shippingCategoryId = (Integer) checkoutData.get("shippingCategoryId");
        Integer paymentCategoryId = (Integer) checkoutData.get("paymentCategoryId");

        // **計算應付款金額，防止前端惡意修改**
        OrderSummaryAmoutDto orderSummary = orderService.calculateOrderSummary(memberId, couponId, shippingCategoryId);
        BigDecimal calculatedTotalAmount = orderSummary.getOrderTotal();

        BigDecimal paymentAmount = null;
        if (checkoutData.containsKey("paymentAmount") && checkoutData.get("paymentAmount") != null) {
            paymentAmount = new BigDecimal(checkoutData.get("paymentAmount").toString());
        }

        String street = (String) checkoutData.get("street");
        String city = (String) checkoutData.get("city");
        String receiverName = (String) checkoutData.get("receiverName");
        String receiverPhone = (String) checkoutData.get("receiverPhone");

        // **建立訂單**
        Map<String, Object> orderResponse = orderService.createOrder(member, memberId, couponId, shippingCategoryId, paymentCategoryId, paymentAmount, street, city, receiverName, receiverPhone);
        Order order = (Order) orderResponse.get("order");

        // **處理信用卡付款，若是信用卡付款則不直接檢查金額**
        if (paymentCategoryId == 1) {
            if (paymentAmount == null || paymentAmount.compareTo(calculatedTotalAmount) != 0) {
                return ResponseEntity.badRequest().body(Map.of("error", "付款金額與訂單金額不符"));
            }

            // 進行信用卡付款處理
            String paymentUrl = paymentService.processCreditCardPayment(order, paymentCategoryId);

            if (paymentUrl != null && paymentUrl.startsWith("<html>")) {
                try {
                    Document doc = Jsoup.parse(paymentUrl);
                    paymentUrl = doc.select("form#paymentForm").attr("action");

                    // 如果支付網址還是空，顯示具體錯誤訊息
                    if (paymentUrl.isEmpty()) {
                        String errorMessage = doc.select("body").text(); // 擷取 HTML 中的錯誤訊息
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                             .body(Map.of("error", "ECPay 回應錯誤: " + errorMessage));
                    }
                } catch (Exception e) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "解析付款頁面失敗", "details", e.getMessage()));
                }
            }


            if ("application/json".equals(acceptHeader)) {
                return ResponseEntity.ok(Map.of("message", "訂單建立成功，請前往付款", "paymentUrl", paymentUrl));
            } else if ("text/html".equals(acceptHeader)) {
                ModelAndView modelAndView = new ModelAndView("checkoutResult"); // 假設 "checkoutResult" 是 HTML 頁面的名稱
                modelAndView.addObject("paymentUrl", paymentUrl);
                return ResponseEntity.ok(modelAndView);
            }
        }

        // 其他情況（例如貨到付款）返回 JSON 或 HTML 格式
        if ("application/json".equals(acceptHeader)) {
            return ResponseEntity.ok(Map.of("message", "訂單建立成功", "orderId", order.getId()));
        } else if ("text/html".equals(acceptHeader)) {
            ModelAndView modelAndView = new ModelAndView("checkoutResult"); // 假設 "checkoutResult" 是 HTML 頁面的名稱
            modelAndView.addObject("order", order);
            return ResponseEntity.ok(modelAndView);
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "無法處理請求"));
    }

    @PostMapping("/payment/ecpay/callback")
    public ResponseEntity<String> handleEcpayCallback(@RequestBody Map<String, String> ecpayResponse) throws Exception {
        String MerchantTradeNo = ecpayResponse.get("MerchantTradeNo");  // 取得訂單編號
        String paymentStatusCode = ecpayResponse.get("RtnCode"); // 付款結果 (1 = 成功)
        String checkValue = ecpayResponse.get("CheckValue");  // 取得回傳的 CheckValue

        // **透過 tradeNo 找到 Order**
        try {
            Integer orderId = Integer.valueOf(MerchantTradeNo); // 將 tradeNo 轉換為 Integer
            Optional<Order> optionalOrder = orderRepo.findById(orderId);

            if (optionalOrder.isPresent()) {
                Order order = optionalOrder.get();
                Payment payment = order.getPayment(); // **取得訂單的付款資訊**

                if (payment != null) {
                    // 驗證 CheckValue
                    boolean isValidCheckValue = false;
					try {
						isValidCheckValue = ecpayUtils.isValidCheckValue(ecpayResponse, checkValue);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    if (!isValidCheckValue) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("CheckValue 驗證失敗！");
                    }

                    PaymentStatus paymentStatus;

                    if ("1".equals(paymentStatusCode)) {
                        // **ECPay 付款成功，設定狀態為 "已付款"**
                        paymentStatus = paymentStatusRepo.findById(2)  // 假設 2 = 已付款
                            .orElseThrow(() -> new IllegalArgumentException("找不到 '已付款' 狀態"));
                    } else {
                        // **ECPay 付款失敗，設定狀態為 "付款失敗"**
                        paymentStatus = paymentStatusRepo.findById(3)  // 假設 3 = 付款失敗
                            .orElseThrow(() -> new IllegalArgumentException("找不到 '付款失敗' 狀態"));
                    }

                    payment.setPaymentStatus(paymentStatus);  // **更新付款狀態**
                    payment.setUpdatedDate(new Date()); // **記錄更新時間**
                    paymentRepo.save(payment);  // **儲存付款狀態**

                    return ResponseEntity.ok("OK");  // **回傳 "OK"，ECPay 需要這個訊息**
                } else {
                    return ResponseEntity.badRequest().body("找不到對應的付款紀錄");
                }
            } else {
                return ResponseEntity.badRequest().body("找不到對應的訂單");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("訂單編號格式錯誤");
        }
    }


//    @GetMapping("/cart")
//    public ResponseEntity<Object> getCartItems(HttpSession session) {
//        Member member = (Member) session.getAttribute("member");
//        Integer memberId = member.getId();
//        List<Cart> cartItems = cartService.getCartItems(memberId);
//        return cartItems.isEmpty()
//            ? ResponseEntity.ok(Map.of("message", "您的購物車是空的，請先選擇商品"))
//            : ResponseEntity.ok(cartItems);
//    }
//
//    @GetMapping("/cart/total")
//    public ResponseEntity<Object> getCartTotal(HttpSession session) {
//        Member member = (Member) session.getAttribute("member");
//        Integer memberId = member.getId();
//        List<Cart> cartItems = cartService.getCartItems(memberId);
//        BigDecimal subtotal = cartService.calculateTotalPrice(cartItems);
//        return ResponseEntity.ok(Map.of("subtotal", subtotal));
//    }
//
//
//
//    @GetMapping("/shipping/categories")
//    public ResponseEntity<Object> getShippingCategories() {
//        List<ShippingCategory> shippingCategories = shippingCategoryRepo.findAll();
//        return ResponseEntity.ok(shippingCategories);
//    }
//
//
//
//    @GetMapping("/payment/categories")
//    public ResponseEntity<Object> getPaymentCategories() {
//        List<PaymentCategory> paymentCategories = paymentCategoryRepo.findAll();
//        return ResponseEntity.ok(paymentCategories);
//    }
//    
//    @GetMapping("/calculate-order-summary")
//    public ResponseEntity<OrderSummaryAmoutDto> calculateOrderSummary(
//    	HttpSession session,
//        @RequestParam(required = false) Integer couponId,
//        @RequestParam(required = false) Integer shippingCategoryId
//    ) {
//    	Member member = (Member) session.getAttribute("member");
//    	Integer memberId = member.getId();
//        OrderSummaryAmoutDto summary = orderService.calculateOrderSummary(memberId, couponId, shippingCategoryId);
//        return ResponseEntity.ok(summary);
//    }
    
}
