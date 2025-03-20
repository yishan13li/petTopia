package petTopia.service.shop;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import petTopia.model.shop.Coupon;
import petTopia.model.shop.MemberCoupon;
import petTopia.model.shop.MemberCouponId;
import petTopia.repository.shop.CouponRepository;
import petTopia.repository.shop.MemberCouponRepository;
import petTopia.repository.shop.OrderRepository;

@Service
public class CouponService {

	 	@Autowired
	    private MemberCouponRepository memberCouponRepo;
	 	
	 	@Autowired
	 	private CouponRepository couponRepo;

	    // 用來更新每個會員的優惠券使用次數
	    public void updateCouponUsageCount(Integer memberId) {
	        // 取得該會員的所有 MemberCoupon 記錄
	        List<MemberCoupon> memberCoupons = memberCouponRepo.findByMemberId(memberId);

	        // 建立一個 Map 用來儲存每個優惠券的使用次數
	        Map<Integer, Integer> couponUsageCountMap = new HashMap<>();
	        
	        // 查詢該會員所有訂單中使用的優惠券次數
	        List<Object[]> usageCounts = couponRepo.countCouponsUsageByMemberId(memberId);
	        
	        // 轉換查詢結果並存入 Map 中
	        for (Object[] result : usageCounts) {
	            Integer couponId = (Integer) result[0];
	            Integer usageCount = ((Long) result[1]).intValue();
	            couponUsageCountMap.put(couponId, usageCount);
	        }

	        // 更新每個 MemberCoupon 的使用次數及狀態
	        for (MemberCoupon memberCoupon : memberCoupons) {
	            Coupon coupon = memberCoupon.getCoupon();
	            Integer couponId = coupon.getId();
	            Integer usageCount = couponUsageCountMap.getOrDefault(couponId, 0);

	            // 更新 usageCount
	            memberCoupon.setUsageCount(usageCount);

	            // 如果使用次數達到上限，將優惠券標記為「不可用」
	            if (coupon.getLimitCount() != null && usageCount >= coupon.getLimitCount()) {
	                memberCoupon.setStatus(false); // 變成不可用
	            } else {
	                memberCoupon.setStatus(true); // 仍然可用
	            }

	            // 更新資料庫
	            memberCouponRepo.save(memberCoupon);
	        }
	    }
	 	    
	    // 用來獲取可用優惠券、過期優惠券與未滿額優惠券
	    public Map<String, List<Coupon>> getCouponsByAmount(Integer memberId, BigDecimal productTotal) {
	        // 獲取會員的所有優惠券
	        List<MemberCoupon> memberCoupons = memberCouponRepo.findByMemberId(memberId);

	        // 可用的優惠券列表
	        List<Coupon> availableCoupons = new ArrayList<>();
	        // 過期或狀態為0的優惠券列表
	        List<Coupon> expiredCoupons = new ArrayList<>();
	        // 未滿額的優惠券列表
	        List<Coupon> notMeetCoupons = new ArrayList<>();

	     // 根據訂單金額和優惠券狀態過濾
	        for (MemberCoupon memberCoupon : memberCoupons) {
	            Coupon coupon = memberCoupon.getCoupon();
	            
	            // 檢查優惠券狀態是否有效且未過期
	            if (coupon.getStatus() != false && memberCoupon.getStatus()!=false) {  // 狀態有效
	                if (productTotal.compareTo(coupon.getMinOrderValue()) >= 0) {
	                    availableCoupons.add(coupon); // 符合條件的優惠券
	                } else {
	                    notMeetCoupons.add(coupon); // 不符合金額條件
	                }
	            } else {
	            	expiredCoupons.add(coupon); // coupon狀態為 0 或membercoupon狀態為0的優惠券
	            }
	        }

	        // 返回包含三個列表的 Map
	        Map<String, List<Coupon>> result = new HashMap<>();
	        result.put("available", availableCoupons);
	        result.put("expired", expiredCoupons);
	        result.put("notMeet", notMeetCoupons);

	        return result;
	    }
	    
	    // 計算使用優惠券會扣到的金額
	    public BigDecimal getDiscountAmountByCoupon(Coupon coupon, BigDecimal productTotal) {
	    	
	        if (coupon == null) return productTotal;
	        BigDecimal discountAmount;

	        if (coupon.getDiscountType()) {
	            // 當 discountType 為 true，為百分比折扣
	            discountAmount = productTotal.multiply(coupon.getDiscountValue()); // 例如：10%折扣 = productTotal * 0.1
	        } else {
	            // 當 discountType 為 false，為固定金額折扣
	            discountAmount = coupon.getDiscountValue(); // 直接減去固定的折扣金額
	        }

	        // 返回最終金額：商品總金額 - 折扣金額
	        return discountAmount.setScale(0, RoundingMode.HALF_UP);
	    }
	    
	    @Transactional
	    public void incrementUsageCount(Integer memberId, Integer couponId) {
	        MemberCoupon memberCoupon = memberCouponRepo.findById(new MemberCouponId())
	            .orElseThrow(() -> new RuntimeException("優惠券不存在"));

	        // 檢查是否已達使用上限
	        if (memberCoupon.getUsageCount() >= memberCoupon.getCoupon().getLimitCount()) {
	            throw new RuntimeException("優惠券已達使用次數上限");
	        }

	        // 增加使用次數
	        memberCoupon.setUsageCount(memberCoupon.getUsageCount() + 1);

	        // 若達上限則設為不可用
	        if (memberCoupon.getUsageCount() >= memberCoupon.getCoupon().getLimitCount()) {
	            memberCoupon.setStatus(false);
	        }

	        // 更新資料庫
	        memberCouponRepo.save(memberCoupon);
	    }

	    public Coupon getCouponById(Integer couponId) {
	    	
	    	Coupon coupon = null;
	    	
	    	Optional<Coupon> couponOpt = couponRepo.findById(couponId);
	    	if (couponOpt.isPresent())
	    		coupon = couponOpt.get();
	    	
	    	return coupon;
	    		
	    }
	    
	    // 購物車獲取優惠券、過期優惠券
	    public Map<String, List<Coupon>> getCoupons(Integer memberId) {
	        // 獲取會員的所有優惠券
	        List<MemberCoupon> memberCoupons = memberCouponRepo.findByMemberId(memberId);

	        // 可用的優惠券列表
	        List<Coupon> availableCoupons = new ArrayList<>();
	        // 過期或狀態為0的優惠券列表
	        List<Coupon> expiredCoupons = new ArrayList<>();

	     // 根據訂單金額和優惠券狀態過濾
	        for (MemberCoupon memberCoupon : memberCoupons) {
	            Coupon coupon = memberCoupon.getCoupon();
	            
	            // 檢查優惠券狀態是否有效且未過期
	            if (coupon.getStatus() != false && memberCoupon.getStatus()!=false) {  // 狀態有效
	            	availableCoupons.add(coupon); // 符合條件的優惠券
	            } else {
	            	expiredCoupons.add(coupon); // coupon狀態為 0 或membercoupon狀態為0的優惠券
	            }
	        }

	        // 返回包含三個列表的 Map
	        Map<String, List<Coupon>> result = new HashMap<>();
	        result.put("available", availableCoupons);
	        result.put("expired", expiredCoupons);

	        return result;
	    }

}
