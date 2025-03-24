package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.shop.ProductColor;

public interface ProductColorRepository extends JpaRepository<ProductColor, Integer>{

	public ProductColor findByName(String name);
	
}
