package petTopia.controller.vendor_admin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import petTopia.model.user.Vendor;
import petTopia.model.user.VendorCategory;
import petTopia.model.vendor.VendorImages;
import petTopia.repository.user.VendorCategoryRepository;
import petTopia.repository.user.VendorRepository;
import petTopia.repository.vendor.VendorImagesRepository;
import petTopia.service.vendor_admin.VendorServiceAdmin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/vendor-admin")
public class VendorProfileController {
	
	private static final Logger logger = LoggerFactory.getLogger(VendorProfileController.class);
	
	@Autowired
	private VendorServiceAdmin vendorService;

	@Autowired
	private VendorRepository vendorRepository;

	@Autowired
	private VendorCategoryRepository categoryRepository;

	@Autowired
	private VendorImagesRepository vendorImagesRepository;

	/**
	 * 根據ID獲取商家資料
	 */
	@GetMapping("/profile/{id}")
    public ResponseEntity<?> getVendorById(@PathVariable Integer id) {
		logger.info("獲取商家資料 - ID: {}", id);
		
        Optional<Vendor> vendorOpt = vendorRepository.findById(id);
        if (vendorOpt.isPresent()) {
			Vendor vendor = vendorOpt.get();
			String vendorLogoImgBase64 = vendorService.getVendorLogoBase64(vendor);
			List<VendorCategory> allcategory = vendorService.getAllVendorCategories();
			int activityCount = vendorService.getActivityCountByVendor(vendor.getId());
			
			Map<String, Object> response = new HashMap<>();
			response.put("vendor", vendor);
			response.put("categories", allcategory);
			response.put("logoBase64", vendorLogoImgBase64);
			response.put("activityCount", activityCount);
			
            return ResponseEntity.ok(response);
        } else {
			logger.warn("商家資料不存在 - ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(Map.of("error", "商家不存在"));
        }
    }
	
	/**
	 * 更新商家資料
	 */
	@PutMapping("/profile/{vendorId}")
	public ResponseEntity<?> updateVendor(
			@PathVariable Integer vendorId,
			@RequestParam(required = false) String vendorName, 
			@RequestParam(required = false) String contactEmail,
			@RequestParam(required = false) String vendorPhone, 
			@RequestParam(required = false) String vendorAddress,
			@RequestParam(required = false) String vendorDescription,
			@RequestParam(required = false) String contactPerson,
			@RequestParam(required = false) String vendorTaxidNumber, 
			@RequestParam(required = false) Integer category,
			@RequestParam(required = false) MultipartFile vendorLogoImg) {
		
		logger.info("更新商家資料 - ID: {}", vendorId);
		
		try {
			// 查找原本的商家資料
			Optional<Vendor> vendorOpt = vendorService.getVendorById(vendorId);
			if (!vendorOpt.isPresent()) {
				logger.warn("商家資料不存在 - ID: {}", vendorId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("error", "商家不存在"));
			}
			
			Vendor vendor = vendorOpt.get();

			// 只更新傳遞過來的欄位
			if (vendorName != null) {
				vendor.setName(vendorName);
			}
			if (contactEmail != null) {
				vendor.setContactEmail(contactEmail);
			}
			if (vendorPhone != null) {
				vendor.setPhone(vendorPhone);
			}
			if (contactPerson != null) {
				vendor.setContactPerson(contactPerson);
			}
			if (vendorAddress != null) {
				vendor.setAddress(vendorAddress);
			}
			if (vendorDescription != null) {
				vendor.setDescription(vendorDescription);
			}
			if (vendorTaxidNumber != null) {
				vendor.setTaxidNumber(vendorTaxidNumber);
			}

			// 更新圖片（如果有）
			if (vendorLogoImg != null && !vendorLogoImg.isEmpty()) {
				try {
					vendor.setLogoImg(vendorLogoImg.getBytes());
				} catch (IOException e) {
					logger.error("圖片上傳失敗", e);
					return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(Map.of("error", "圖片上傳失敗"));
				}
			}

			// 只有在傳遞 category 時才更新它
			if (category != null) {
				Optional<VendorCategory> categoryOpt = categoryRepository.findById(category);
				if (!categoryOpt.isPresent()) {
					logger.warn("分類不存在 - ID: {}", category);
					return ResponseEntity.status(HttpStatus.BAD_REQUEST)
						.body(Map.of("error", "分類不存在"));
				}
				vendor.setVendorCategoryId(category);
			}

			// 保存更新過的資料
			Vendor updatedVendor = vendorService.updateVendor(vendor);
			logger.info("商家資料更新成功 - ID: {}", vendorId);

			return ResponseEntity.ok(Map.of(
				"message", "商家資料更新成功",
				"vendor", updatedVendor
			));
		} catch (Exception e) {
			logger.error("商家資料更新失敗", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("error", "商家資料更新失敗：" + e.getMessage()));
		}
	}

	/**
	 * 獲取商家Logo圖片
	 */
	@GetMapping("/profile/{vendorId}/logo")
	public ResponseEntity<byte[]> getProfileImage(@PathVariable Integer vendorId) {
		logger.info("獲取商家Logo圖片 - ID: {}", vendorId);
		
		try {
			Optional<Vendor> vendorOpt = vendorService.getVendorById(vendorId);
			if (!vendorOpt.isPresent()) {
				logger.warn("商家不存在 - ID: {}", vendorId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}
			
			Vendor vendor = vendorOpt.get();
			byte[] imageBytes = vendor.getLogoImg();
			if (imageBytes == null || imageBytes.length == 0) {
				logger.warn("商家Logo圖片不存在 - ID: {}", vendorId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);

			return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("獲取商家Logo圖片失敗", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	/**
	 * 獲取商家圖片ID列表
	 */
	@GetMapping("/profile/{vendorId}/images")
	public ResponseEntity<?> getVendorImages(@PathVariable Integer vendorId) {
		logger.info("獲取商家圖片ID列表 - ID: {}", vendorId);
		
		try {
			Optional<Vendor> vendorOpt = vendorRepository.findById(vendorId);
			if (!vendorOpt.isPresent()) {
				logger.warn("商家不存在 - ID: {}", vendorId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND)
					.body(Map.of("error", "商家不存在"));
			}

			Vendor vendor = vendorOpt.get();
			List<VendorImages> images = vendor.getImages();
			List<Map<String, Object>> imagesList = new ArrayList<>();

			for (VendorImages image : images) {
				Map<String, Object> imageMap = new HashMap<>();
				imageMap.put("id", image.getId());
				imageMap.put("url", "/api/vendor-admin/images/" + image.getId());
				imagesList.add(imageMap);
			}

			return ResponseEntity.ok(Map.of("images", imagesList));
		} catch (Exception e) {
			logger.error("獲取商家圖片ID列表失敗", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("error", "獲取商家圖片失敗：" + e.getMessage()));
		}
	}

	/**
	 * 獲取特定圖片
	 */
	@GetMapping("/images/{imageId}")
	public ResponseEntity<byte[]> getImage(@PathVariable Integer imageId) {
		logger.info("獲取特定圖片 - ID: {}", imageId);
		
		try {
			Optional<VendorImages> imageOpt = vendorImagesRepository.findById(imageId);
			if (!imageOpt.isPresent()) {
				logger.warn("圖片不存在 - ID: {}", imageId);
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
			}

			VendorImages image = imageOpt.get();
			byte[] imageFile = image.getImage();

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG);

			return new ResponseEntity<>(imageFile, headers, HttpStatus.OK);
		} catch (Exception e) {
			logger.error("獲取圖片失敗", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}
	
	/**
	 * 獲取所有商家分類
	 */
	@GetMapping("/categories")
	public ResponseEntity<?> getAllCategories() {
		logger.info("獲取所有商家分類");
		
		try {
			List<VendorCategory> categories = vendorService.getAllVendorCategories();
			return ResponseEntity.ok(Map.of("categories", categories));
		} catch (Exception e) {
			logger.error("獲取商家分類失敗", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.body(Map.of("error", "獲取商家分類失敗：" + e.getMessage()));
		}
	}
}
