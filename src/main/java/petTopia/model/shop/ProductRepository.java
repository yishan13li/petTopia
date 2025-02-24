package petTopia.model.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer>{

	public Product findFirstByProductDetailIdOrderByIdAsc(Integer productDetailId);
	
}
