package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer>, ProductDetailRepositoryCustom {

//	@Query("SELECT pd FROM ProductDetail pd WHERE pd.id = :productId")
//	public ProductDetail findByProductId(@Param("productId") Integer proudctId);

	// 模糊搜尋 不分大小寫
	List<ProductDetail> findByNameContainingIgnoreCase(String keyword);

	ProductDetail findByName(String name);
	
}
