package petTopia.controller.shop;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import petTopia.dto.shop.ProductDetailDto;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;
import petTopia.service.shop.ProductService;

@RestController
@RequestMapping("/manage/shop")
public class ManageProductController {
	
	@Autowired
	private ProductService productService;
	
    // 後台商品管理 => 查詢所有商品
	@GetMapping("/products")
	public ResponseEntity<?> getShopProducts(
			@RequestParam Integer start, 
			@RequestParam Integer rows, 
			@RequestParam Optional<String> keyword, 
			@RequestParam String category, 
			@RequestParam Optional<String> status
			) {
		
		Map<String, Object> responseBody = new HashMap<>();
		Map<String, Object> filterData = new HashMap<>();
		
		filterData.put("keyword", keyword.isPresent() ? keyword.get() : "");
		filterData.put("category", category);
		filterData.put("status", status.isPresent() ? status.get() : "");
		
		filterData.put("start", start);
		filterData.put("rows", rows);
		System.out.println(filterData);
		long count = productService.getProductsCount(filterData);
		responseBody.put("count", count);

//		List<Product> productList = productService.getProducts(filterData);
		
		
		
//		responseBody.put("count", 0);
//		responseBody.put("productList", productList);
		
		return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.OK);
		
	}
	
}
