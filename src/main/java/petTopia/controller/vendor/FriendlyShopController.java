package petTopia.controller.vendor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import petTopia.model.vendor.FriendlyShop;
import petTopia.model.vendor.VendorCategory;
import petTopia.service.vendor.FriendlyShopService;
import petTopia.service.vendor.VendorCategoryService;

@RestController
public class FriendlyShopController {

	@Autowired
	private FriendlyShopService friendlyShopService;

	@Autowired
	private VendorCategoryService vendorCategoryService;

	@GetMapping("/api/vendor/{vendorId}/coordinate")
	public ResponseEntity<FriendlyShop> getCoordinateByVendorId(@PathVariable Integer vendorId) {
		FriendlyShop friendlyShop = friendlyShopService.findFirstByVendorId(vendorId);
		return ResponseEntity.ok(friendlyShop);
	}

	@GetMapping("/api/vendor/all/coordinate")
	public ResponseEntity<List<FriendlyShop>> getAllCoordinates() {
		List<FriendlyShop> friendlyShopList = friendlyShopService.findAll();
		return ResponseEntity.ok(friendlyShopList);
	}

	@PostMapping("/api/vendor/coordinate/find")
	public ResponseEntity<List<FriendlyShop>> findByKeyword(@RequestBody Map<String, String> data) {
		String keyword = data.get("keyword");
		List<FriendlyShop> friendlyShopList = friendlyShopService.findByKeyword(keyword);
		return ResponseEntity.ok(friendlyShopList);
	}

	@GetMapping("/api/vendor/coordinate/find/vendor/{vendorId}")
	public ResponseEntity<List<FriendlyShop>> findByVendorId(@PathVariable Integer vendorId) {
		List<FriendlyShop> friendlyShopList = friendlyShopService.findByVendorId(vendorId);
		return ResponseEntity.ok(friendlyShopList);
	}

	@GetMapping("/api/vendor/coordinate/find/category/{categoryId}")
	public ResponseEntity<List<FriendlyShop>> findByVendorCategory(@PathVariable Integer categoryId) {
		List<FriendlyShop> friendlyShopList = friendlyShopService.findByCategoryId(categoryId);
		return ResponseEntity.ok(friendlyShopList);
	}

	@PostMapping("/api/vendor/coordinate")
	public ResponseEntity<?> findByAddress(@RequestParam String address) {
		BigDecimal[] coordinate = friendlyShopService.getLatLng(address);
		return ResponseEntity.ok(coordinate);
	}

	@PostMapping("/api/vendor/friendly_shop/add")
	public ResponseEntity<?> addFriendlyShop(@RequestBody Map<String, Object> data) {
		String name = (String) data.get("name");
		String address = (String) data.get("address");
		Integer categoryId = (Integer) data.get("categoryId");

		FriendlyShop friendlyShop = friendlyShopService.addFriendlyShop(name, categoryId, address);
		return ResponseEntity.ok(friendlyShop);
	}

	@PutMapping("/api/vendor/friendly_shop/{id}/modify")
	public ResponseEntity<?> addFriendlyShop(@PathVariable Integer id, @RequestBody Map<String, Object> data) {
		String name = (String) data.get("name");
		String address = (String) data.get("address");
		Integer categoryId = ((Number) data.get("categoryId")).intValue();

		FriendlyShop friendlyShop = friendlyShopService.modifyFriendlyShop(id, name, categoryId, address);
		return ResponseEntity.ok(friendlyShop);
	}

	@DeleteMapping("/api/vendor/friendly_shop/{id}/delete")
	public ResponseEntity<?> addFriendlyShop(@PathVariable Integer id) {
		friendlyShopService.deleteFriendlyShop(id);
		return ResponseEntity.ok("成功刪除");
	}

	@GetMapping("/api/vendor/friendly_shop/{id}")
	public ResponseEntity<FriendlyShop> getFriendlyShop(@PathVariable Integer id) {
		FriendlyShop friendlyShop = friendlyShopService.getFriendlyShop(id);
		return ResponseEntity.ok(friendlyShop);
	}

	@GetMapping("/api/vendor/category/for/friendly_shop")
	public ResponseEntity<?> getAllCategories() {
		List<VendorCategory> categoryList = vendorCategoryService.findAllIncludeNoVendor();
		return ResponseEntity.ok(categoryList);
	}
}
