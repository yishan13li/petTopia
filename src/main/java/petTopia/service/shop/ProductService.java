package petTopia.service.shop;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.shop.Product;
import petTopia.model.shop.ProductCategory;
import petTopia.model.shop.ProductColor;
import petTopia.model.shop.ProductDetail;
import petTopia.model.shop.ProductSize;
import petTopia.repository.shop.ProductCategoryRepository;
import petTopia.repository.shop.ProductColorRepository;
import petTopia.repository.shop.ProductDetailRepository;
import petTopia.repository.shop.ProductRepository;
import petTopia.repository.shop.ProductSizeRepository;

@Service
public class ProductService {

	@Autowired
    private ProductRepository productRepository;
    @Autowired
    private ProductDetailRepository productDetailRepository;
    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    private ProductColorRepository productColorRepository;
    @Autowired
    private ProductSizeRepository productSizeRepository;
	
	
	public Product findById(Integer productId) {
		Optional<Product> productOpt = productRepository.findById(productId);
		if (productOpt.isPresent()) {
			return productOpt.get();
		}
		return null;
	}
	
	// 獲取有上架的商品
	public List<Product> getAvailableProductByProductDetailId(Integer productDetailId, Boolean status) {
		List<Product> productList = productRepository.findByProductDetailIdAndStatus(productDetailId, status);
		if (productList != null && productList.size() != 0) {
			return productList;
		}
		
		return null;
	}
	
	public Product getMinPriceProduct(Integer productDetailId) {
		Product product = productRepository.findFirstByProductDetailIdOrderByUnitPriceAsc(productDetailId);
		if (product != null) {
			return product;
		}
		return null;
	}
	
	public Product getMaxPriceProduct(Integer productDetailId) {
		Product product = productRepository.findFirstByProductDetailIdOrderByUnitPriceDesc(productDetailId);
		if (product != null) {
			return product;
		}
		return null;
	}
	
	public List<Product> findByProductSizeId(Integer productSizeId){
		List<Product> productList = productRepository.findByProductSizeId(productSizeId);
		if (productList != null && productList.size() != 0) {
			return productList;
		}
		
		return null;
	}
	
	public List<Product> findByProductColorId(Integer productColorId){
		List<Product> productList = productRepository.findByProductColorId(productColorId);
		if (productList != null && productList.size() != 0) {
			return productList;
		}
		
		return null;
	}
	
	public List<Product> findByProductDetailIdAndSizeId(Integer productDetailId, Integer productSizeId){
		List<Product> productList = productRepository.findByProductDetailIdAndProductSizeId(productDetailId, productSizeId);
		if (productList != null && productList.size() != 0) {
			return productList;
		}
		
		return null;
	}
	
	public List<Product> findByProductDetailIdAndColorId(Integer productDetailId, Integer productColorId){
		List<Product> productList = productRepository.findByProductDetailIdAndProductColorId(productDetailId, productColorId);
		if (productList != null && productList.size() != 0) {
			return productList;
		}
		
		return null;
	}
	
	public Product getConfirmProduct(
			Integer productDetailId, 
			Integer productSizeId, 
			Integer productColorId) {
		Product product = productRepository.findByProductDetailIdAndProductSizeIdAndProductColorId(productDetailId, productSizeId, productColorId);
		if (product != null) {
			return product;
		}
		return null;
	}
	
	public Product findFirstByProductDetailId(Integer productDetailId) {
		Product product = productRepository.findFirstByProductDetailIdOrderByIdAsc(productDetailId);
		if (product != null) {
			return product;
		}
		return null;
	}
	
	
	
	public Product insertProduct(String name, String description, 
			String categoryName, String colorName, String sizeName) {
		//FIXME: 還未完成
		
		// 檢查或創建商品分類 (ProductCategory)
        ProductCategory productCategory = productCategoryRepository.findByName(categoryName);
        if (productCategory == null) {
        	productCategory = new ProductCategory();
        	productCategory.setName(categoryName);
            productCategoryRepository.save(productCategory);
        }
        
        // 創建商品細節 (ProductDetail)
        ProductDetail productDetail = new ProductDetail();
        productDetail.setName(name);
        productDetail.setDescription(description);
        productDetail.setProductCategory(productCategory);
        productDetail = productDetailRepository.save(productDetail);

        // 取得顏色 (ProductColor)
        ProductColor productColor = productColorRepository.findByName(colorName);
        if (productColor == null) {
//            throw new RuntimeException("顏色不存在: " + colorName);
        	return null;
        }
        
        // 取得尺寸 (ProductSize)
        ProductSize productSize = productSizeRepository.findByName(sizeName);
        if (productSize == null) {
//            throw new RuntimeException("尺寸不存在: " + sizeName);
            return null;
        }
		
        // 創建商品 (Product)
        Product product = new Product();
        product.setStockQuantity(100);
        product.setUnitPrice(new BigDecimal("500"));
        product.setStatus(true);
        product.setProductDetail(productDetail);
        product.setProductColor(productColor);
        product.setProductSize(productSize);
        
        
        // 存入資料庫
        return productRepository.save(product);
		
	}
	
	

}
