package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.shop.ProductSize;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Integer>{

	public ProductSize findByName(String name);
	
}
