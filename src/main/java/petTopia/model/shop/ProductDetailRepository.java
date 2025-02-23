package petTopia.model.shop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductDetailRepository extends JpaRepository<ProductDetail, Integer>{

//	@Query("SELECT pd FROM ProductDetail pd WHERE pd.id = :productId")
//	public ProductDetail findByProductId(@Param("productId") Integer proudctId);
}
