package petTopia.service.shop;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;
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
public class ManageProductService {
	
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
	
	// 根據條件搜尋商品的總數
	public Long getProductsCount(Map<String, Object> filterData){
		try {
			JSONObject jsonObj = new JSONObject(filterData);
			return productRepository.count(jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
		
	// 根據條件搜尋商品
	public List<Product> getProducts(Map<String, Object> filterData){
		try {
			JSONObject jsonObj = new JSONObject(filterData);
			return productRepository.find(jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// 新增商品 or 更新商品
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
