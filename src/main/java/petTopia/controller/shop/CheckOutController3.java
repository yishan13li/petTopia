package petTopia.controller.shop;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpSession;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Coupon;
import petTopia.model.shop.PaymentCategory;
import petTopia.model.shop.ShippingAddress;
import petTopia.model.shop.ShippingCategory;
import petTopia.model.user.Member;
import petTopia.service.shop.CartService;
import petTopia.service.shop.CouponService;
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
import petTopia.repository.shop.ShippingAddressRepository;

@RestController
@RequestMapping("/api2/shop")
public class CheckOutController3 {

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

    @GetMapping("/cart")
    public ResponseEntity<Object> getCartItems(HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        Integer memberId = member.getId();
        List<Cart> cartItems = cartService.getCartItems(memberId);
        return cartItems.isEmpty()
            ? ResponseEntity.ok(Map.of("message", "您的購物車是空的，請先選擇商品"))
            : ResponseEntity.ok(cartItems);
    }

    @GetMapping("/cart/total")
    public ResponseEntity<Object> getCartTotal(HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        Integer memberId = member.getId();
        List<Cart> cartItems = cartService.getCartItems(memberId);
        BigDecimal total = cartService.calculateTotalPrice(cartItems);
        return ResponseEntity.ok(Map.of("total", total));
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

    @GetMapping("/shipping/categories")
    public ResponseEntity<Object> getShippingCategories() {
        List<ShippingCategory> shippingCategories = shippingCategoryRepo.findAll();
        return ResponseEntity.ok(shippingCategories);
    }

    @GetMapping("/coupons")
    public ResponseEntity<Object> getCoupons(HttpSession session) {
        Member member = (Member) session.getAttribute("member");
        Integer memberId = member.getId();
        List<Cart> cartItems = cartService.getCartItems(memberId);
        BigDecimal total = cartService.calculateTotalPrice(cartItems);

        // 更新優惠券使用次數
        couponService.updateCouponUsageCount(memberId);
        Map<String, List<Coupon>> couponsMap = couponService.getCouponsByAmount(memberId, total);

        List<Coupon> availableCoupons = couponsMap.get("available");
        List<Coupon> notMeetCoupons = couponsMap.get("notMeet");

        return ResponseEntity.ok(Map.of(
            "availableCoupons", availableCoupons,
            "notMeetCoupons", notMeetCoupons
        ));
    }

    @GetMapping("/payment/categories")
    public ResponseEntity<Object> getPaymentCategories() {
        List<PaymentCategory> paymentCategories = paymentCategoryRepo.findAll();
        return ResponseEntity.ok(paymentCategories);
    }
    
    @GetMapping("/checkout")
    public ResponseEntity<Object> checkoutPage(HttpSession session) {
        Member member = (Member) session.getAttribute("member");

        // 直接從各個分開的 API 中獲取資料
        List<Cart> cartItems = cartService.getCartItems(member.getId());
        BigDecimal total = cartService.calculateTotalPrice(cartItems);
        ShippingAddress lastShippingAddress = shippingAddressRepo.findByMemberAndIsCurrent(member, true);
        List<ShippingCategory> shippingCategories = shippingCategoryRepo.findAll();
        Map<String, List<Coupon>> couponsMap = couponService.getCouponsByAmount(member.getId(), total);
        List<PaymentCategory> paymentCategories = paymentCategoryRepo.findAll();

        Map<String, Object> responseData = Map.of(
            "paymentCategories", paymentCategories,
            "availableCoupons", couponsMap.get("available"),
            "notMeetCoupons", couponsMap.get("notMeet"),
            "member", member,
            "cartItems", cartItems,
            "total", total,
            "lastAddress", lastShippingAddress,
            "shippingCategories", shippingCategories
        );

        return ResponseEntity.ok(responseData);
    }


}
