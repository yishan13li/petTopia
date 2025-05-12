package petTopia.repository.shop;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;

public interface ProductRepository extends JpaRepository<Product, Integer>, ProductRepositoryCustom{
	
	public Product findFirstByProductDetailIdOrderByUnitPriceAsc(Integer productDetailId);
	public Product findFirstByProductDetailIdOrderByUnitPriceDesc(Integer productDetailId);
	
	public Product findFirstByProductDetailIdOrderByIdAsc(Integer productDetailId);
	
	public List<Product> findByProductSizeId(Integer productSizeId);
	public List<Product> findByProductColorId(Integer productColorId);
	
	public List<Product> findByProductDetailIdAndProductSizeId(Integer productDetailId, Integer productSizeId);
	public List<Product> findByProductDetailIdAndProductColorId(Integer productDetailId, Integer productColorId);
	

	public Product findByProductDetailIdAndProductSizeIdAndProductColorId(
		    Integer productDetailId,
		    Integer productSizeId,
		    Integer productColorId);
	
	public List<Product> findAllByIdIn(List<Integer> productIds);
	
	// 獲取有上架的商品
	public List<Product> findByProductDetailIdAndStatus(Integer productDetailId, Boolean status);
	
	public ProductDetail findByProductDetailId(Integer productDetailId);
	
	// 統計總商品數
	@Query("SELECT COUNT(p) FROM Product p")
	public long countTotalProducts();
	
    // 查詢庫存小於 50 的商品數量
    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity < 50")
    long countLowStockProducts();
	
    //購買商品更動庫存
    // 使用 @Lock 註解指定悲觀鎖
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :productId")
    Product lockProduct(@Param("productId") Integer productId);
}
