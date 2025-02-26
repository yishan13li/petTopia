// package petTopia.service.user;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import petTopia.model.user.CouponBean;
// import petTopia.repository.user.CouponRepository;

// import java.time.LocalDateTime;
// import java.util.List;

// @Service
// public class CouponService {
    
//     @Autowired
//     private CouponRepository couponRepository;
    
//     // 查找所有有效的優惠券
//     public List<CouponBean> findActiveCoupons() {
//         LocalDateTime now = LocalDateTime.now();
//         return couponRepository.findByStatusTrueAndValidStartBeforeAndValidEndAfterAndUsedCountLessThan(
//             now, now, 0);
//     }
    
//     // 根據ID查找優惠券
//     public CouponBean findById(Integer id) {
//         return couponRepository.findById(id).orElse(null);
//     }
    
//     // 創建新優惠券
//     @Transactional
//     public CouponBean createCoupon(CouponBean coupon) {
//         return couponRepository.save(coupon);
//     }
    
//     // 驗證優惠券
//     public boolean isCouponValid(CouponBean coupon) {
//         if (coupon == null) return false;
        
//         LocalDateTime now = LocalDateTime.now();
//         return coupon.getStatus() &&                           // 優惠券已啟用
//                now.isAfter(coupon.getValidStart()) &&         // 在有效期內
//                now.isBefore(coupon.getValidEnd()) &&          // 在有效期內
//                coupon.getUsedCount() < coupon.getLimitCount();// 未超過使用次數
//     }
    
//     // 使用優惠券
//     @Transactional
//     public void useCoupon(CouponBean coupon, Integer orderAmount) {
//         // 檢查優惠券是否有效
//         if (!isCouponValid(coupon)) {
//             throw new IllegalStateException("優惠券無效");
//         }
        
//         // 檢查訂單金額是否達到最低消費
//         if (coupon.getMinOrderValue() != null && 
//             orderAmount < coupon.getMinOrderValue()) {
//             throw new IllegalStateException(
//                 "訂單金額未達到最低消費金額：" + coupon.getMinOrderValue());
//         }
        
//         // 更新使用次數
//         coupon.setUsedCount(coupon.getUsedCount() + 1);
//         couponRepository.save(coupon);
//     }
    
//     // 計算折扣金額
//     public Integer calculateDiscount(CouponBean coupon, Integer orderAmount) {
//         if (coupon.getDiscountType() == CouponBean.DiscountType.fixed) {
//             return coupon.getDiscountValue().intValue();
//         } else {
//             // 百分比折扣
//             return orderAmount * coupon.getDiscountValue().intValue() / 100;
//         }
//     }
    
//     // 其他方法...
// } 