package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import petTopia.model.shop.Shipping;

@Repository
public interface ShippingRepository extends JpaRepository<Shipping, Integer>{

	Shipping findByOrderId(Integer orderId);
	
}
