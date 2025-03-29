package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import petTopia.model.shop.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {

    // 查詢某會員對某優惠券的使用次數
    @Query("SELECT COUNT(o) FROM Order o WHERE o.member.id = :memberId AND o.coupon.id = :couponId")
    Integer countByMemberIdAndCouponId(Integer memberId, Integer couponId);

    // 查詢某會員所有訂單中，每個優惠券的使用次數
    @Query("SELECT o.coupon.id, COUNT(o) FROM Order o WHERE o.member.id = :memberId AND o.orderStatus.id != 6 GROUP BY o.coupon.id")
    List<Object[]> countCouponsUsageByMemberId(Integer memberId);

    // 當訂單取消時，將優惠券的使用次數減少 1
    @Modifying
    @Transactional
    @Query("UPDATE MemberCoupon c SET c.usageCount = c.usageCount - 1 " +
    	       "WHERE c.member.id = :memberId AND c.coupon.id = :couponId")
    void decreaseCouponUsageCount(Integer memberId, Integer couponId);

    // 按名稱搜尋優惠券（分頁）
    Page<Coupon> findByNameContaining(String name, Pageable pageable);
}
