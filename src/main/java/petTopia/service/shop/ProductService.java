package petTopia.service.shop;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.shop.Product;
import petTopia.model.shop.ProductCategory;
import petTopia.model.shop.ProductCategoryRepository;
import petTopia.model.shop.ProductColor;
import petTopia.model.shop.ProductColorRepository;
import petTopia.model.shop.ProductDetail;
import petTopia.model.shop.ProductDetailRepository;
import petTopia.model.shop.ProductRepository;
import petTopia.model.shop.ProductSize;
import petTopia.model.shop.ProductSizeRepository;

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
	
    
	public Product insertProduct(String name, String description, 
			String categoryName, String colorName, String sizeName) {
		
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

	public Product findFirstByProductDetailId(Integer productDetailId) {
		Product product = productRepository.findFirstByProductDetailIdOrderByIdAsc(productDetailId);
		if (product != null) {
			return product;
		}
		return null;
	}
	
}
