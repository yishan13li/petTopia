package petTopia.service.shop;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import petTopia.model.shop.Product;
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
	
	// 搜尋商品
	public List<ProductDetail> searchProductByKeywords(String keywordString){
		List<ProductDetail> productDetailList = productDetailRepository.searchProducts(keywordString);
		if (productDetailList != null && productDetailList.size() != 0) {
			return productDetailList;
		}
		
		return null;
	}
		
}
