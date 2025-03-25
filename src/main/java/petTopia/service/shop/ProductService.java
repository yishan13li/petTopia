package petTopia.service.shop;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.dto.shop.ProductDto;
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
	
	// 批量更新狀態
	public List<Product> updateProductsStatus(List<Integer> productIds, String batchStatus){
		List<Product> productList = productRepository.findAllByIdIn(productIds);
		
		for (Product product : productList) {
			boolean setBatchStatus = "1".equals(batchStatus) ? true : false;
			product.setStatus(setBatchStatus);
			
			productRepository.save(product);
		}
		
		return productList;
		
	}
	
	// 新增商品
	public Product insertProduct(ProductDto productDto){
		
//		Product existingProduct = productRepository
//				.findByProductDetailIdAndProductSizeIdAndProductColorId(
//						product.getProductDetail().getId(), 
//				product.getProductSize().getId(), 
//				product.getProductColor().getId());
//		
//		// 商品已存在 
//		if (existingProduct != null) {
//			return existingProduct;
//		}
		
		Product product = new Product();
		
		// find ProductCategory
		String categoryName = productDto.getProductDetail().getProductCategory().getName();
		ProductCategory productCategory = productCategoryRepository.findByName(categoryName);
		
		// set ProductDetail
		ProductDetail productDetail = productDetailRepository.findByName(productDto.getProductDetail().getName());
		if (productDetail == null) {
			productDetail = new ProductDetail();
	        productDetail.setName(productDto.getProductDetail().getName());
	        productDetail.setDescription(productDto.getProductDetail().getDescription());
	        productDetail.setProductCategory(productCategory);
	        productDetailRepository.save(productDetail);
		}
		
		product.setProductDetail(productDetail);
		product.getProductDetail().setProductCategory(productCategory);
        
        ProductSize productSize = productSizeRepository.findByName(productDto.getProductSize().getName());
        if (productSize == null) {
        	productSize = new ProductSize();
        	productSize.setName(productDto.getProductSize().getName());
        	productSizeRepository.save(productSize);
        }
		
        ProductColor productColor = productColorRepository.findByName(productDto.getProductColor().getName());
        if (productColor == null) {
        	productColor = new ProductColor();
        	productColor.setName(productDto.getProductColor().getName());
        	productColorRepository.save(productColor);
        }
		
        product.setProductSize(productSize);
        product.setProductColor(productColor);
        product.setUnitPrice(productDto.getUnitPrice());
        product.setDiscountPrice(productDto.getDiscountPrice());
        product.setStockQuantity(productDto.getStockQuantity());
        product.setStatus(productDto.getStatus() == 1 ? true : false);
        product.setPhoto(null);
        
		return null;
		
	}
		
		

}
