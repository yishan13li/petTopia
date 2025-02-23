package petTopia.model.shop;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductColorRepository extends JpaRepository<ProductColor, Integer>{

	public ProductColor findByName(String name);
	
}
