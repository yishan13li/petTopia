package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.MemberCoupon;
import petTopia.model.shop.MemberCouponId;

import java.util.List;

@Repository
public interface MemberCouponRepository extends JpaRepository<MemberCoupon, MemberCouponId> {
    List<MemberCoupon> findByMemberId(Integer memberId);

}
