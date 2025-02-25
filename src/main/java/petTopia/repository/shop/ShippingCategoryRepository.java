package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.ShippingCategory;

@Repository
public interface ShippingCategoryRepository extends JpaRepository<ShippingCategory, Integer> {
}
