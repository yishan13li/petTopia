package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import petTopia.model.shop.Product;

public interface ProductRepository extends JpaRepository<Product, Integer>{
	
	public List<Product> findByProductDetailId(Integer productDetailId);
	
	public Product findFirstByProductDetailIdOrderByUnitPriceAsc(Integer productDetailId);
	public Product findFirstByProductDetailIdOrderByUnitPriceDesc(Integer productDetailId);
	
	public Product findFirstByProductDetailIdOrderByIdAsc(Integer productDetailId);
	
}
