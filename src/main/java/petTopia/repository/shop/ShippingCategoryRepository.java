package petTopia.repository.shop;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.ShippingCategory;

@Repository
public interface ShippingCategoryRepository extends JpaRepository<ShippingCategory, Integer> {
    @Query("SELECT s.name FROM ShippingCategory s")
	List<String> findAllShippingCategory();
    
    Optional<ShippingCategory> findByName(String name);
}
