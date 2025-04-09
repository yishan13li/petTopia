package petTopia.service.shop;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.shop.ProductDetail;
import petTopia.repository.shop.ProductDetailRepository;


@Service
public class ProductDetailService {

	@Autowired
	private ProductDetailRepository productDetailRepository;
	
	public List<ProductDetail> findAll(){
		List<ProductDetail> allProductDetail = productDetailRepository.findAll();
		
		if (allProductDetail != null && allProductDetail.size() != 0) {
			return allProductDetail;
			
		}
		
		return null;
	}
	
	public ProductDetail findByProductDetailId(Integer productDetailId) {
		Optional<ProductDetail> productDetailOpt = productDetailRepository.findById(productDetailId);
		if (productDetailOpt.isPresent()) {
			ProductDetail productDetail = productDetailOpt.get();
			
			return productDetail;
		}
		
		return null;
	}
	
	// 根據條件搜尋商品的總數
	public Long getProductsCount(Map<String, Object> filterData){
		try {
			JSONObject jsonObj = new JSONObject(filterData);
			return productDetailRepository.count(jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
		
	// 根據條件搜尋商品
	public List<ProductDetail> getProducts(Map<String, Object> filterData){
		try {
			JSONObject jsonObj = new JSONObject(filterData);
			return productDetailRepository.find(jsonObj);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	// 關鍵字搜尋商品(舊版)
	public List<ProductDetail> searchProductByKeywords(String keywordString){
		List<ProductDetail> productDetailList = productDetailRepository.searchProducts(keywordString);
		if (productDetailList != null && productDetailList.size() != 0) {
			return productDetailList;
		}
		
		return null;
	}
		
	public ProductDetail findByProductDetailName(String productDetailName) {
		
		ProductDetail productDetail = productDetailRepository.findByName(productDetailName);
		if (productDetail != null)
			return productDetail;
		
		return null;
	}
}
