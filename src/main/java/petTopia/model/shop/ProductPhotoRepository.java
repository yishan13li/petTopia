package petTopia.model.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductPhotoRepository extends JpaRepository<ProductPhoto, Integer>{

	public ProductPhoto findFirstByProductDetailIdOrderByIdAsc(Integer productId);
	
	public List<ProductPhoto> findByProductDetailId(Integer productId);
}
