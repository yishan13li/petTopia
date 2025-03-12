package petTopia.controller.shop;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import petTopia.dto.shop.OrderSummaryAmoutDto;
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
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
import petTopia.repository.shop.ShippingAddressRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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
    @ResponseBody
    public ResponseEntity<?> processCheckout(@RequestBody Map<String, Object> checkoutData, HttpSession session)  {
        
        Member member = (Member) session.getAttribute("member");
        Integer memberId = member.getId();
        
        Integer couponId = checkoutData.get("couponId") != null ? (Integer) checkoutData.get("couponId") : null;
        Integer shippingCategoryId = (Integer) checkoutData.get("shippingCategoryId");
        Integer paymentCategoryId = (Integer) checkoutData.get("paymentCategoryId");
        
        // **根據購物車計算應付款金額，防止前端惡意修改**
        OrderSummaryAmoutDto orderSummary = orderService.calculateOrderSummary(memberId, couponId, shippingCategoryId);
        BigDecimal calculatedTotalAmount = orderSummary.getOrderTotal(); // 取得訂單總金額
           
        // **安全檢查 paymentAmount，貨到付款時允許為 null**
        BigDecimal paymentAmount = null;
        if (checkoutData.containsKey("paymentAmount") && checkoutData.get("paymentAmount") != null) {
            paymentAmount = new BigDecimal(checkoutData.get("paymentAmount").toString());
        }

        // **檢查信用卡付款金額是否與訂單金額一致**
        if (paymentCategoryId == 1) { // 1 = 信用卡付款
        	if (paymentAmount == null || paymentAmount.compareTo(calculatedTotalAmount) != 0) {
        		return ResponseEntity.badRequest().body(Map.of("error", "付款金額與訂單金額不符"));
        	}
        }
        
        String street = (String) checkoutData.get("street");
        String city = (String) checkoutData.get("city");
        String receiverName = (String) checkoutData.get("receiverName");
        String receiverPhone = (String) checkoutData.get("receiverPhone");
        
        // 建立訂單
        Order order = orderService.createOrder(member, memberId, couponId, shippingCategoryId, paymentCategoryId, paymentAmount, street, city , receiverName, receiverPhone);
        
//        // **信用卡付款時，跳轉至支付頁面**
//        if (paymentCategoryId == 1) {
//            String paymentUrl = paymentService.generatePaymentUrl(order);
//            return ResponseEntity.ok(Map.of("message", "請前往支付", "paymentUrl", paymentUrl));
//        }
        
        return ResponseEntity.ok(Map.of("message", "訂單建立成功", "orderId", order.getId()));
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
