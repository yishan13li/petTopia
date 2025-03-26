package petTopia.controller.shop;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import petTopia.dto.shop.PaymentResponseDto;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Coupon;
import petTopia.model.shop.Order;
import petTopia.model.shop.PaymentCategory;
import petTopia.model.shop.ShippingAddress;
import petTopia.model.shop.ShippingCategory;
import petTopia.model.user.Member;
import petTopia.service.shop.CartService;
import petTopia.service.shop.CouponService;
import petTopia.service.shop.OrderService;
import petTopia.service.shop.PaymentService;
import petTopia.service.user.MemberService;
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
import petTopia.repository.shop.ShippingAddressRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/shop")
public class CheckOutController {

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
    private MemberService memberService;

    @GetMapping("/checkout")
    public ResponseEntity<Object> getCheckoutInfo(@RequestParam List<Integer> productIds, @RequestParam Integer memberId) {
        
        List<Cart> cartItems = cartService.getCartByMemberIdAndProductIds(memberId, productIds);
        BigDecimal subtotal = cartService.calculateTotalPrice(memberId,productIds);
        List<ShippingCategory> shippingCategories = shippingCategoryRepo.findAll();
        List<PaymentCategory> paymentCategories = paymentCategoryRepo.findAll();
        
        Map<String, Object> response = new HashMap<>();
        response.put("cartItems", cartItems.isEmpty() ? Collections.emptyList() : cartItems);
        response.put("subtotal", subtotal);
        response.put("shippingCategories", shippingCategories);
        response.put("paymentCategories", paymentCategories);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/member")
    public ResponseEntity<Object> getMemberInfo(@RequestParam Integer memberId) {
    	Optional<Member> member = memberService.findById(memberId);
    	
    	if (member.isEmpty()) {
          return ResponseEntity.status(HttpStatus.NOT_FOUND)
          .body(Map.of("message", "未找到會員資料"));
    	}

        // 若會員資料存在，返回該會員資訊
        return ResponseEntity.ok(member.get());
    }
    
    @GetMapping("/shipping/address")
    public ResponseEntity<Object> getShippingAddress(@RequestParam Integer memberId) {
    	Optional<Member> memberOpt = memberService.findById(memberId);
    	
    	Member member=memberOpt.get();
    	
        ShippingAddress lastShippingAddress = shippingAddressRepo.findByMemberAndIsCurrent(member, true);
        if (lastShippingAddress == null) {
            lastShippingAddress = new ShippingAddress();  // 避免前端渲染錯誤
        }
        return ResponseEntity.ok(lastShippingAddress);
    }
    
    
    @GetMapping("/coupons")
    public ResponseEntity<Object> getCoupons(@RequestParam List<Integer> productIds, @RequestParam Integer memberId) {
    	
        BigDecimal subtotal = cartService.calculateTotalPrice(memberId,productIds);
        
        // 更新優惠券使用次數
        couponService.updateCouponUsageCount(memberId);
        Map<String, List<Coupon>> couponsMap = couponService.getCouponsByAmount(memberId, subtotal);

        List<Coupon> availableCoupons = couponsMap.get("available");
        List<Coupon> notMeetCoupons = couponsMap.get("notMeet");
        
        // 獲取選取的優惠券
        Map<String, Object> response = new HashMap<>();
        response.put("availableCoupons", availableCoupons);
        response.put("notMeetCoupons", notMeetCoupons);

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/checkout")
    public ResponseEntity<?> processCheckout(@RequestBody Map<String, Object> checkoutData, 
    										@RequestParam Integer memberId,
                                              @RequestHeader(value = "Accept", defaultValue = "application/json") String acceptHeader) throws Exception {

    	Optional<Member> memberOpt = memberService.findById(memberId);
    	
    	Member member= memberOpt.get();
    	
        // 從 checkoutData 取得各種資料
        Integer couponId = checkoutData.get("couponId") != null ? (Integer) checkoutData.get("couponId") : null;
        Integer shippingCategoryId = (Integer) checkoutData.get("shippingCategoryId");
        Integer paymentCategoryId = (Integer) checkoutData.get("paymentCategoryId");

        // 取得前端傳來的購物車 ID 清單
        @SuppressWarnings("unchecked")
		List<Integer> productIdList = ((List<Map<String, Object>>) checkoutData.get("cartItems"))
                .stream()
                .map(item -> (Integer) item.get("productId")) // 假設每個 item 中有 productId 欄位
                .collect(Collectors.toList());
        
        // 取得收件人資料
        String receiverName = (String) checkoutData.get("receiverName");
        String receiverPhone = (String) checkoutData.get("receiverPhone");
        String street = (String) checkoutData.get("street");
        String city = (String) checkoutData.get("city");
        String amount = (String) checkoutData.get("paymentAmount");

        BigDecimal paymentAmount = (amount != null) ? new BigDecimal(amount) : null;

        // 建立訂單，將收件資訊傳入
        Map<String, Object> orderResponse = orderService.createOrder(member, memberId, couponId, shippingCategoryId, paymentCategoryId, paymentAmount, street, city, receiverName, receiverPhone,productIdList);
        Order order = (Order) orderResponse.get("order");

        // 如果選擇信用卡付款 (paymentCategoryId == 1)
        if (paymentCategoryId == 1) {
            // 調用 processCreditCardPayment 來處理付款並返回支付參數資料
            PaymentResponseDto paymentResponse = paymentService.processCreditCardPayment(order, paymentCategoryId);

            if (paymentResponse != null) {
                // 如果請求頭是 JSON 格式，返回支付參數資料
                if ("application/json".equals(acceptHeader)) {
                    return ResponseEntity.ok(Map.of(
                        "message", "訂單建立成功，請前往付款",
                        "paymentData", paymentResponse
                    ));
                }
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "付款頁面生成失敗"));
            }
        }

        // 如果付款方式不是信用卡 (paymentCategoryId != 1)，直接返回訂單建立成功
        return ResponseEntity.ok(Map.of("message", "訂單建立成功，請查看訂單詳情",
                "orderId", order.getId()));
    }

    @PostMapping("/payment/ecpay/callback")
    public ResponseEntity<String> handleEcpayCallback(@RequestParam Map<String, String> callbackParams) {
        
    	try {
            // 呼叫 Service 層處理回調邏輯
            String response = paymentService.handleEcpayCallback(callbackParams);
            
            return ResponseEntity.ok(response); // 確保回應是 "1|OK" 或 "0|Error: XXX"
        } catch (Exception e) {
            return ResponseEntity.ok("0|Error: " + e.getMessage()); // 發生錯誤時，仍符合 ECPay 格式
        }
    }

}
