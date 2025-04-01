package petTopia.controller.vendor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import petTopia.model.vendor.FriendlyShop;
import petTopia.service.vendor.FriendlyShopService;

@RestController
public class FriendlyShopController {

	@Autowired
	private FriendlyShopService friendlyShopService;

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

//	@GetMapping("/api/vendor/coordinate/find/category/null")
//	public ResponseEntity<List<FriendlyShop>> findByVendorCategoryNull() {
//		List<FriendlyShop> friendlyShopList = friendlyShopService.findByCategoryIdNull();
//		return ResponseEntity.ok(friendlyShopList);
//	}

	@PostMapping("/api/vendor/coordinate")
	public ResponseEntity<?> findByAddress(@RequestParam String address) {
		double[] coordinate = friendlyShopService.getLatLng(address);

		Map<String, Object> resopnse = new HashMap<>();
		resopnse.put("response", coordinate);
		return ResponseEntity.ok(resopnse);
	}
}
