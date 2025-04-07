package petTopia.controller.shop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import petTopia.dto.shop.ProductDetailDto;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;
import petTopia.service.shop.ProductDetailService;
import petTopia.service.shop.ProductReviewService;
import petTopia.service.shop.ProductService;

@Controller
@RequestMapping("/shop/products")
public class ShopProductsController {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Autowired
	private ProductService productService;
	
	@Autowired
	private ProductDetailService productDetailService;
	
	@Autowired
	private ProductReviewService productReviewService;

	// 商品瀏覽頁面 vue
	@ResponseBody
	@GetMapping
	public ResponseEntity<?> getShopProducts(
			@RequestParam Integer start, 
			@RequestParam Integer rows, 
			@RequestParam String category, 
			@RequestParam Optional<String> keyword) {
		
		Map<String, Object> responseBody = new HashMap<>();
		Map<String, Object> filterData = new HashMap<>();
		
		filterData.put("category", category);
		filterData.put("keyword", keyword.isPresent() ? keyword.get() : "");
		
		filterData.put("start", start);
		filterData.put("rows", rows);
		
		long count = productDetailService.getProductsCount(filterData);
		responseBody.put("count", count);

		List<ProductDetail> productDetailList = productDetailService.getProducts(filterData);
		
		// 獲取ProductDetail一樣的所有Product，選出最低價
		List<ProductDetailDto> productDetailDtoList = new ArrayList<ProductDetailDto>();
		if (productDetailList != null) {
			for (ProductDetail productDetail : productDetailList) {
				
				ProductDetailDto productDetailDto = new ProductDetailDto();
				
				List<Product> productList = productService.getAvailableProductByProductDetailId(productDetail.getId(), true);
				if (productList != null) {
					
					// 獲取productList內最低價商品
					Product minPriceProduct = productList.stream()
						    .min(Comparator.comparing(p -> 
						        p.getDiscountPrice() != null 
						        ? p.getUnitPrice().min(p.getDiscountPrice()) 
						        : p.getUnitPrice(), 
						        Comparator.naturalOrder()
						    )).orElse(null);
					
					productDetailDto.setMinPriceProduct(minPriceProduct);
				}
				productDetailDto.setProductDetail(productDetail);
				productDetailDto.setAvgRating(productReviewService.getAverageRatingByProductDetailId(productDetail.getId()));
				
				productDetailDtoList.add(productDetailDto);
			}
			
			responseBody.put("productDetailDtoList", productDetailDtoList);
			
			return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.OK);
		}
		
		responseBody.put("count", 0);
		responseBody.put("productDetailDtoList", null);
		
		return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.OK);
		
	}
	
	// 商品瀏覽頁面 => 獲取商品資訊的第一個商品的圖片 
	@GetMapping("/api/getPhoto")
	public ResponseEntity<?> getProductPhoto(
			@RequestParam Integer productDetailId) {
		
		Product product = productService.findFirstByProductDetailId(productDetailId);
		
		byte[] photo = product.getPhoto();
		
		if (photo != null && photo.length != 0) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			
			return new ResponseEntity<byte[]>(photo, headers, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
	}
	
	
	
	
}
