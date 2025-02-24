package petTopia.model.shop;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Integer>{

//	public ProductPhoto findFirstByProductDetailIdOrderByIdAsc(Integer productId);
	
	public ProductPhoto findByProductId(Integer productId);
}
