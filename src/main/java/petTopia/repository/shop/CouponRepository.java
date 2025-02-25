package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.Coupon;
import petTopia.model.shop.ShippingCategory;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Integer> {
}
