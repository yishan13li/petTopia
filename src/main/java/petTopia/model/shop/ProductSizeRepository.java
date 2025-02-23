package petTopia.model.shop;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductSizeRepository extends JpaRepository<ProductSize, Integer>{

	public ProductSize findByName(String name);
	
}
