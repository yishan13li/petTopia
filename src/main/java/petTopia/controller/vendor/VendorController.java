package petTopia.controller.vendor;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCategory;
import petTopia.model.vendor.VendorImages;
import petTopia.service.vendor.VendorCategoryService;
import petTopia.service.vendor.VendorImagesService;
import petTopia.service.vendor.VendorReviewService;
import petTopia.service.vendor.VendorService;

/**
 * 商家相關的控制器
 * 處理所有與商家相關的API請求
 */
@CrossOrigin
@RestController
public class VendorController {

	@Autowired
	private VendorService vendorService;

	@Autowired
	private VendorReviewService vendorReviewService;
	
	@Autowired
	private VendorImagesService vendorImagesService;
	
	@Autowired
	private VendorCategoryService vendorCategoryService;

	/**
	 * 獲取特定商家的詳細資訊
	 * @param vendorId 商家ID
	 * @return 商家詳細資訊
	 */
	@GetMapping("/api/vendor/{vendorId}")
	public ResponseEntity<Vendor> getVendorDetail(@PathVariable Integer vendorId) {
		Vendor vendor = vendorService.findVendorById(vendorId);
		return ResponseEntity.ok(vendor);
	}

	/**
	 * 獲取所有商家的列表
	 * @return 所有商家的列表
	 */
	@GetMapping("/api/vendor/all")
	public ResponseEntity<List<Vendor>> getAllVendors() {
		List<Vendor> vendorList = vendorService.findAllVendor();
		return ResponseEntity.ok(vendorList);
	}

	/**
	 * 獲取除了指定商家外的所有商家列表
	 * @param vendorId 要排除的商家ID
	 * @return 其他商家的列表
	 */
	@GetMapping("/api/vendor/all/except/{vendorId}")
	public ResponseEntity<List<Vendor>> getAllVendorsExceptOne(@PathVariable Integer vendorId) {
		List<Vendor> vendorList = vendorService.findAllVendorExceptOne(vendorId);
		return ResponseEntity.ok(vendorList);
	}

	/**
	 * 獲取特定商家的圖片列表
	 * @param vendorId 商家ID
	 * @return 商家的圖片列表
	 */
	@GetMapping("/api/vendor/{vendorId}/image")
	public ResponseEntity<List<VendorImages>> getVendorImages(@PathVariable Integer vendorId) {
		List<VendorImages> imageList = vendorImagesService.findImageListByVendorId(vendorId);
		return ResponseEntity.ok(imageList);
	}

	/**
	 * 根據分類ID獲取商家列表
	 * @param categoryId 分類ID
	 * @return 該分類下的所有商家列表
	 */
	@GetMapping("/api/vendor/category/{categoryId}")
	public ResponseEntity<List<Vendor>> getVendorsByCategory(@PathVariable Integer categoryId) {
		List<Vendor> vendorList = vendorService.findVendorByCategoryId(categoryId);
		return ResponseEntity.ok(vendorList);
	}
	
	/**
	 * 獲取特定分類下除了指定商家外的所有商家
	 * @param categoryId 分類ID
	 * @param vendorId 要排除的商家ID
	 * @return 該分類下的其他商家列表
	 */
	@GetMapping("/api/vendor/category/{categoryId}/except/vendor/{vendorId}")
	public ResponseEntity<List<Vendor>> getVendorsByCategoryExceptOne(@PathVariable Integer categoryId,
			@PathVariable Integer vendorId) {
		List<Vendor> vendorList = vendorService.findVendorByCategoryIdExceptOne(categoryId, vendorId);
		return ResponseEntity.ok(vendorList);
	}

	/**
	 * 根據名稱或描述搜尋商家
	 * @param keyword 搜尋關鍵字
	 * @return 符合條件的商家列表
	 */
	@PostMapping(value = "/api/vendor/find", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<List<Vendor>> getVendorsByNameOrDescription(@RequestParam String keyword) {
		List<Vendor> vendorList = vendorService.findVendorByNameOrDescription(keyword);
		return ResponseEntity.ok(vendorList);
	}
	
	/**
	 * 獲取所有商家分類
	 * @return 所有商家分類列表
	 */
	@GetMapping("/api/vendor/category/show")
	public ResponseEntity<List<VendorCategory>> getAllCategories() {
		List<VendorCategory> categoryList = vendorCategoryService.findAllVendorCategory();
		return ResponseEntity.ok(categoryList);
	}
	
	/**
	 * 更新商家的評分
	 * @param vendorId 商家ID
	 * @return 更新後的商家資訊
	 */
	@GetMapping("/api/vendor/{vendorId}/update/rating")
	public ResponseEntity<Vendor> updateVendorRating(@PathVariable Integer vendorId) {
		Vendor vendor = vendorReviewService.setAverageRating(vendorId);
		return ResponseEntity.ok(vendor);
	}
}
