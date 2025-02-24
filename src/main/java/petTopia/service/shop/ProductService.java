package petTopia.service.shop;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import petTopia.model.shop.Product;
import petTopia.model.shop.ProductCategory;
import petTopia.model.shop.ProductCategoryRepository;
import petTopia.model.shop.ProductColor;
import petTopia.model.shop.ProductColorRepository;
import petTopia.model.shop.ProductDetail;
import petTopia.model.shop.ProductDetailRepository;
import petTopia.model.shop.ProductPhoto;
import petTopia.model.shop.ProductPhotoRepository;
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
	
    @Autowired
    private ProductPhotoRepository productPhotoRepository;
    
    
    
    /**
     * 
     * @param name 商品名稱
     * @param description 商品敘述
     * @param categoryName 商品分類名稱
     * @param colorName 商品顏色名稱
     * @param sizeName 商品尺寸名稱
     * @param photos 商品照片
     * @return Product
     */
	public Product insertProduct(String name, String description, 
			String categoryName, String colorName, String sizeName, 
			List<ProductPhoto> photos) {
		
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
        
        // 新增商品照片 (ProductPhoto)
        if (photos != null && !photos.isEmpty()) {
            for (ProductPhoto photo : photos) {
                photo.setProduct(product); // 設定關聯
            }
            productPhotoRepository.saveAll(photos); // 一次儲存所有照片
        }
        
        // 存入資料庫
        return productRepository.save(product);
		
	}

	public ProductPhoto addProductPhotoByProductId(Integer productId, MultipartFile file) throws IOException {
		Optional<Product> productOpt = productRepository.findById(productId);
		if (productOpt.isPresent()) {
			
			ProductPhoto productPhoto = productPhotoRepository.findByProductId(productId);
			
			productPhoto.setProduct(productOpt.get());
			productPhoto.setPhoto(file.getBytes());
			
			productPhotoRepository.save(productPhoto);
			
			return productPhoto;
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
	
}
