package petTopia.controller.shop;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.persistence.criteria.Order;
import jakarta.servlet.http.HttpSession;
import petTopia.model.shop.Cart;
import petTopia.model.shop.Coupon;
import petTopia.model.shop.PaymentCategory;
import petTopia.model.shop.ShippingAddress;
import petTopia.model.shop.ShippingCategory;
import petTopia.model.user.Member;
import petTopia.repository.shop.PaymentCategoryRepository;
import petTopia.repository.shop.PaymentRepository;
import petTopia.repository.shop.ShippingAddressRepository;
import petTopia.repository.shop.ShippingCategoryRepository;
import petTopia.repository.shop.ShippingRepository;
import petTopia.service.shop.CartService;
import petTopia.service.shop.CouponService;
import petTopia.service.shop.OrderService;
import petTopia.service.shop.PaymentService;
import petTopia.service.shop.ShippingService;

@Controller
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
    
    @GetMapping("/shop/checkout")
    public String checkoutPage(HttpSession session, Model model) {
        // 從 session 取得 userId 和 member 資訊
        Member member = (Member) session.getAttribute("member");
    
        // 使用 member 中的 id 獲取購物車商品
        Integer memberId = member.getId();
        List<Cart> cartItems = cartService.getCartItems(memberId);

        if (cartItems.isEmpty()) {
            // 如果購物車是空的，可以顯示一個提示訊息
            model.addAttribute("message", "您的購物車是空的，請先選擇商品");
            return "shop/checkout";  // 顯示空購物車頁面
        }

        // 計算購物車總金額
        BigDecimal total = cartService.calculateTotalPrice(cartItems);

        // 獲取最近一次的訂單地址
        ShippingAddress lastShippingAddress = shippingAddressRepo.findByMemberAndIsCurrent(member, true);
        if (lastShippingAddress == null) {
            lastShippingAddress = new ShippingAddress();  // 避免 Thymeleaf 出錯
        }
        
        // 獲取所有配送方式資料
        List<ShippingCategory> shippingCategories = shippingCategoryRepo.findAll();

        // 先更新會員的優惠券使用次數
        couponService.updateCouponUsageCount(memberId);

        // 獲取可用、未滿額的優惠券，並將它們放入 model
        Map<String, List<Coupon>> couponsMap = couponService.getCouponsByAmount(memberId, total);

        // 可用的優惠券
        List<Coupon> availableCoupons = couponsMap.get("available");
        // 未滿額的優惠券
        List<Coupon> notMeetCoupons = couponsMap.get("notMeet");

        // 這裡不需要過期的優惠券，若有需要可以解開註解
//        List<Coupon> expiredCoupons = couponsMap.get("expired");

        // 獲取所有配送方式資料
        List<PaymentCategory> paymentCategories = paymentCategoryRepo.findAll();

        
        // 將會員資訊、購物車商品和總金額傳遞給model
        model.addAttribute("paymentCategories", paymentCategories);
        model.addAttribute("availableCoupons", availableCoupons);
        model.addAttribute("notMeetCoupons", notMeetCoupons);
        model.addAttribute("member", member);
        model.addAttribute("cartItems", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("lastAddress", lastShippingAddress);
        model.addAttribute("shippingCategories", shippingCategories);
        return "shop/shop_checkout";
    }
    
}
