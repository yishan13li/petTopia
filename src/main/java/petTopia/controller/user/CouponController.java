// package petTopia.controller.user;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.bind.annotation.ResponseBody;

// import petTopia.model.user.CouponBean;
// import petTopia.service.user.CouponService;

// import java.math.BigDecimal;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;

// @Controller
// public class CouponController {
    
//     @Autowired
//     private CouponService couponService;
    
//     // 顯示優惠券列表頁面
//     @GetMapping("/coupons")
//     public String showCoupons(Model model) {
//         List<CouponBean> activeCoupons = couponService.findActiveCoupons();
//         model.addAttribute("coupons", activeCoupons);
//         return "coupon_list";
//     }
    
//     // 檢查優惠券是否可用（AJAX）
//     @PostMapping("/check-coupon")
//     @ResponseBody
//     public Map<String, Object> checkCoupon(
//             @RequestParam Integer orderAmount,
//             @RequestParam Integer couponId) {
        
//         Map<String, Object> response = new HashMap<>();
        
//         try {
//             CouponBean coupon = couponService.findById(couponId);
            
//             if (coupon == null) {
//                 response.put("valid", false);
//                 response.put("message", "優惠券不存在");
//                 return response;
//             }
            
//             // 檢查優惠券是否有效
//             if (!couponService.isCouponValid(coupon)) {
//                 response.put("valid", false);
//                 response.put("message", "優惠券已過期或已被使用完");
//                 return response;
//             }
            
//             // 檢查訂單金額是否達到最低消費
//             if (coupon.getMinOrderValue() != null && 
//                 new BigDecimal(orderAmount).compareTo(coupon.getMinOrderValue()) < 0) {
//                 response.put("valid", false);
//                 response.put("message", "未達到最低消費金額：" + coupon.getMinOrderValue());
//                 return response;
//             }
            
//             // 計算折扣金額
//             Integer discountAmount = couponService.calculateDiscount(coupon, orderAmount);
            
//             response.put("valid", true);
//             response.put("discountAmount", discountAmount);
//             response.put("message", "優惠券可使用，折抵金額：" + discountAmount);
            
//         } catch (Exception e) {
//             response.put("valid", false);
//             response.put("message", "系統錯誤：" + e.getMessage());
//         }
        
//         return response;
//     }
// } 