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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import petTopia.dto.shop.ProductDetailDto;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;
import petTopia.service.shop.ProductService;

@RestController
@RequestMapping("/manage/shop/products")
public class ManageProductController {
	
	@Autowired
	private ProductService productService;
	
    // 後台商品管理 => 查詢所有商品
	@GetMapping
	public ResponseEntity<?> getShopProducts(
			@RequestParam Integer start, 
			@RequestParam Integer rows, 
			@RequestParam Optional<String> keyword, 
			@RequestParam Optional<String> category, 
			@RequestParam Optional<String> status, 
			@RequestParam Optional<String> isProductDiscount, 
			@RequestParam Optional<String> stockQuantityLessThan, 
			@RequestParam Optional<String> startDate, 
			@RequestParam Optional<String> endDate
			) {
		
		Map<String, Object> responseBody = new HashMap<>();
		Map<String, Object> filterData = new HashMap<>();
		
		filterData.put("keyword", keyword.isPresent() ? keyword.get() : "");
		filterData.put("category", category.isPresent() ? category.get() : "");
		filterData.put("status", status.isPresent() ? status.get() : "");
		filterData.put("isProductDiscount", isProductDiscount.isPresent() ? isProductDiscount.get() : "");
		filterData.put("stockQuantityLessThan", stockQuantityLessThan.isPresent() ? stockQuantityLessThan.get() : "");
		filterData.put("startDate", startDate.isPresent() ? startDate.get() : "");
		filterData.put("endDate", endDate.isPresent() ? endDate.get() : "");
		
		filterData.put("start", start);
		filterData.put("rows", rows);
//		System.out.println(filterData);
		// 獲取商品總數
		long count = productService.getProductsCount(filterData);
		responseBody.put("count", count);
		// 獲取商品
		List<Product> productList = productService.getProducts(filterData);
		if (productList != null ) {
			responseBody.put("productList", productList);
		}
		else {
			responseBody.put("productList", null);
		}
		
		
		return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.OK);
		
	}
	
	
	// 後台商品管理 => 批量更新狀態
	@PutMapping("/api/updateProductsStatus")
	public ResponseEntity<?> updateProductsStatus(
			@RequestParam List<Integer> productIds,
            @RequestParam Optional<String> batchStatus
			) {
		
		Map<String, Object> responseBody = new HashMap<>();
		
		String batchStatusStr = batchStatus.isPresent() ? batchStatus.get() : "";
		
		List<Product> productList = productService.updateProductsStatus(productIds, batchStatusStr);
		if (productList != null) {
			return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.OK);
		}
		
		
		return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.OK);
		
	}
		
	// 後台商品管理 => 新增商品
	@PostMapping("/api/insertProduct")
	public ResponseEntity<?> insertProduct(
			@RequestBody Map<String, Object> product
			) {
		
		Map<String, Object> responseBody = new HashMap<>();
		
		System.out.println(product);
		//TODO: 製作productDTO
		
		return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.OK);
		
	}
			
}
