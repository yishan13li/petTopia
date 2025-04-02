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

import petTopia.dto.vendor.VendorDto;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCategory;
import petTopia.model.vendor.VendorImages;
import petTopia.service.vendor.VendorCategoryService;
import petTopia.service.vendor.VendorImagesService;
import petTopia.service.vendor.VendorService;

@CrossOrigin
@RestController
public class VendorController {

	@Autowired
	private VendorService vendorService;

	@Autowired
	private VendorImagesService vendorImagesService;

	@Autowired
	private VendorCategoryService vendorCategoryService;

	@GetMapping("/api/vendor/{vendorId}")
	public ResponseEntity<Vendor> getVendorDetail(@PathVariable Integer vendorId) {
		Vendor vendor = vendorService.findVendorById(vendorId);
		return ResponseEntity.ok(vendor);
	}

	@GetMapping("/api/vendor/all")
	public ResponseEntity<List<Vendor>> getAllVendors() {
		List<Vendor> vendorList = vendorService.findAllVendor();
		return ResponseEntity.ok(vendorList);
	}

	@GetMapping("/api/vendor/all/except/{vendorId}")
	public ResponseEntity<List<Vendor>> getAllVendorsExceptOne(@PathVariable Integer vendorId) {
		List<Vendor> vendorList = vendorService.findAllVendorExceptOne(vendorId);
		return ResponseEntity.ok(vendorList);
	}

	@GetMapping("/api/vendor/{vendorId}/image")
	public ResponseEntity<List<VendorImages>> getVendorImages(@PathVariable Integer vendorId) {
		List<VendorImages> imageList = vendorImagesService.findImageListByVendorId(vendorId);
		return ResponseEntity.ok(imageList);
	}

	@GetMapping("/api/vendor/category/{categoryId}")
	public ResponseEntity<List<Vendor>> getVendorsByCategory(@PathVariable Integer categoryId) {
		List<Vendor> vendorList = vendorService.findVendorByCategoryId(categoryId);
		return ResponseEntity.ok(vendorList);
	}

	@GetMapping("/api/vendor/category/{categoryId}/except/vendor/{vendorId}")
	public ResponseEntity<List<Vendor>> getVendorsByCategoryExceptOne(@PathVariable Integer categoryId,
			@PathVariable Integer vendorId) {
		List<Vendor> vendorList = vendorService.findVendorByCategoryIdExceptOne(categoryId, vendorId);
		return ResponseEntity.ok(vendorList);
	}

	@PostMapping(value = "/api/vendor/find", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<List<Vendor>> getVendosrByNameOrDescription(@RequestParam String keyword) {
		List<Vendor> vendorList = vendorService.findVendorByNameOrDescription(keyword);
		return ResponseEntity.ok(vendorList);
	}

	@GetMapping("/api/vendor/category/show")
	public ResponseEntity<List<VendorCategory>> getAllCategories() {
		List<VendorCategory> categoryList = vendorCategoryService.findAllVendorCategory();
		return ResponseEntity.ok(categoryList);
	}

	@GetMapping("/api/vendor/all/for/swiper")
	public ResponseEntity<List<VendorDto>> getAllVendorsForSwiper() {
		List<VendorDto> dtoList = vendorService.getAllVendorDto();
		return ResponseEntity.ok(dtoList);
	}
}
