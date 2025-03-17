package petTopia.controller.shop;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import petTopia.dto.shop.ProductDetailDto;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;
import petTopia.service.shop.ProductDetailService;
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
	

	// 商品瀏覽頁面 vue
	@ResponseBody
	@GetMapping
	public ResponseEntity<?> showShopProducts(@RequestParam Optional<String> keyword) {
		
		ObjectNode responseBody = objectMapper.createObjectNode();
		
		// 搜尋字串
		String searchKeyword = "";
		if (keyword.isPresent()) 
			searchKeyword = keyword.get();
		
		List<ProductDetail> productDetailList = new ArrayList<>();
		// 獲取商品資訊(ProductDetail)
		if (!"".equals(searchKeyword)) {
			productDetailList = productDetailService.searchProductByKeywords(searchKeyword);			
		}
		else 
			productDetailList = productDetailService.findAll();	
		
		
		// 獲取ProductDetail一樣的所有Poduct，選出最低價
		List<ProductDetailDto> productDetailDtoList = new ArrayList<ProductDetailDto>();
		if (productDetailList != null) {
			for (ProductDetail productDetail : productDetailList) {
				
				ProductDetailDto productDetailDto = new ProductDetailDto();
				
				List<Product> productList = productService.findByProductDetailId(productDetail.getId());
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
				
				productDetailDtoList.add(productDetailDto);
			}
			
			JsonNode productDetailDtoListJson  = objectMapper.convertValue(productDetailDtoList, JsonNode.class);
			
			responseBody.set("productDetailListDto", productDetailDtoListJson);
			
			return new ResponseEntity<List<ProductDetailDto>>(productDetailDtoList, HttpStatus.OK);
		}
		
		productDetailDtoList = null;
		
		return new ResponseEntity<List<ProductDetailDto>>(productDetailDtoList, HttpStatus.OK);
		
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
