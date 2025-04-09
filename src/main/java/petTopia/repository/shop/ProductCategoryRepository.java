package petTopia.repository.shop;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.shop.ProductCategory;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, Integer>{

	public ProductCategory findByName(String name);
	
	
}
