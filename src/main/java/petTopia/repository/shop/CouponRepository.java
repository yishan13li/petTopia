package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.Coupon;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {

    // 查詢某會員對某優惠券的使用次數
    @Query("SELECT COUNT(o) FROM Order o WHERE o.member.id = :memberId AND o.coupon.id = :couponId")
    Integer countByMemberIdAndCouponId(Integer memberId, Integer couponId);

    // 查詢某會員所有訂單中，每個優惠券的使用次數
    @Query("SELECT o.coupon.id, COUNT(o) FROM Order o WHERE o.member.id = :memberId GROUP BY o.coupon.id")
    List<Object[]> countCouponsUsageByMemberId(Integer memberId);

}
