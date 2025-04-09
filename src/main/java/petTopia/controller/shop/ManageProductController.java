package petTopia.controller.shop;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import petTopia.dto.shop.ProductDetailDto;
import petTopia.dto.shop.ProductDto;
import petTopia.dto.shop.ProductDto2;
import petTopia.model.shop.Product;
import petTopia.model.shop.ProductDetail;
import petTopia.service.shop.ProductDetailService;
import petTopia.service.shop.ProductService;

@RestController
@RequestMapping("/manage/shop/products")
public class ManageProductController {
	
	@Autowired
	private ProductService productService;
	@Autowired
	private ProductDetailService productDetailService;
	
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
			@RequestPart ProductDto product, 
			@RequestPart MultipartFile photo
			) {
		
		Map<String, Object> responseBody = new HashMap<>();
		
		ProductDto productDto = product;
		
		try {
			productDto.setPhoto(photo.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(productDto);
		
		if (productService.insertProduct(productDto))
			responseBody.put("messages", "新增商品成功");
		else
			responseBody.put("messages", "同樣商品已存在");
		
		return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.OK);
		
	}
	
	// 後台商品管理 => 修改商品
	@PostMapping("/api/modifyProduct")
	public ResponseEntity<?> modifyProduct(
			@RequestPart ProductDto2 product, 
			@RequestPart(required = false) MultipartFile photo
			) {
		
		Map<String, Object> responseBody = new HashMap<>();
		
		ProductDto2 productDto = product;
		
		if (photo != null && !photo.isEmpty()) {
			try {
				productDto.setPhoto(photo.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			Product p = productService.findById(productDto.getId());
			productDto.setPhoto(p.getPhoto());
		}
		
//		System.out.println(productDto);
		
		Product modifyProduct = productService.modifyProduct(productDto);
		
		if (modifyProduct != null) {
			responseBody.put("modifyProduct", modifyProduct);
			responseBody.put("messages", "修改商品成功");
		}
		else {
			responseBody.put("modifyProduct", null);
			responseBody.put("messages", "修改商品失敗");
		}
		
		return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.OK);
		
	}
	
	// 後台商品管理 => 新增商品 => 如果有同名商品直接獲取Description
	@GetMapping("/api/insertProduct/getDescription")
	public ResponseEntity<?> getProductDetailDescription(
			@RequestParam String productDetailName
			) {
		
		Map<String, Object> responseBody = new HashMap<>();
		
		ProductDetail productDetail = productDetailService.findByProductDetailName(productDetailName);
		if (productDetail != null)
			responseBody.put("description", productDetail.getDescription());
		else
			responseBody.put("description", "");
		
		
		return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.OK);
		
	}
	
	// 後台商品管理 => 修改商品 => 獲取商品
	@GetMapping("/api/modifyProduct/getProduct")
	public ResponseEntity<?> getProduct(
			@RequestParam Integer productId
			) {

		Product product = productService.findById(productId);
		
		return new ResponseEntity<Product>(product, HttpStatus.OK);
		
	}
		
	// 後台商品管理 => 修改商品 => 獲取商品照片
	@GetMapping("/api/modifyProduct/getProductPhoto")
	public ResponseEntity<?> getProductPhoto(
			@RequestParam Integer productId
			) {
		Product product = productService.findById(productId);
		
		byte[] photo = product.getPhoto();
		
		if (photo != null && photo.length != 0) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);
			
			return new ResponseEntity<byte[]>(photo, headers, HttpStatus.OK);
		}
		
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
	}
			
	// 後台商品管理 => 刪除商品
	@GetMapping("/api/deleteProduct")
	public ResponseEntity<?> deleteProduct(
			@RequestParam Integer productId
			) {
		Map<String, Object> responseBody = new HashMap<>();
		
		if (productService.deleteProduct(productId)) {
			responseBody.put("messages", "刪除商品成功");
		}
		else {
			responseBody.put("messages", "刪除商品失敗");
		}
		
		return new ResponseEntity<Map<String, Object>>(responseBody, HttpStatus.OK);
		
	}	
	
	
}
