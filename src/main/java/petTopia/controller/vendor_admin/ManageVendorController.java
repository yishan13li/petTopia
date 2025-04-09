package petTopia.controller.vendor_admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCategory;
import petTopia.service.vendor_admin.ManageVendorService;
import petTopia.service.vendor_admin.VendorServiceAdmin;

@Controller
public class ManageVendorController {

	@Autowired
	private ManageVendorService managevendorService;

	@Autowired
	private VendorServiceAdmin vendorServiceAdmin;

	@ResponseBody
	@GetMapping("/api/admin/vendors/allcategory")
	public ResponseEntity<?> getAllCategory() {
		Map<String, Object> response = new HashMap<>();
		List<VendorCategory> allcategory = vendorServiceAdmin.getAllVendorCategories();
		response.put("allcategory", allcategory);

		return ResponseEntity.ok(response);
	}

	/**
	 * 📌 1. 取得所有店家（可分類） - `/api/vendors` → 全部店家 - `/api/vendors?categoryId=1` →
	 * 根據類別篩選
	 */
	@ResponseBody
	@GetMapping("/api/admin/vendors")
	public ResponseEntity<List<Vendor>> getAllVendors(
	        @RequestParam(required = false) Integer categoryId, 
	        @RequestParam(required = false) Boolean status) {

	    List<Vendor> vendors = managevendorService.getAllVendors(categoryId, status);
	    return ResponseEntity.ok(vendors);
	}

	/**
	 * 📌 2. 單個店家狀態更新 - `/api/vendors/{id}/status` - `PUT` 請求 + `{ "status":
	 * true/false }`
	 */
	@PutMapping("/api/admin/vendors/status/{id}")
	public ResponseEntity<String> updateVendorStatus(@PathVariable Integer id,
			@RequestBody VendorStatusRequest request) {
		boolean success = managevendorService.updateVendorStatus(id, request.isStatus());
		return success ? ResponseEntity.ok("狀態已更新") : ResponseEntity.status(HttpStatus.NOT_FOUND).body("店家不存在");
	}

	/**
	 * 📌 3. 批量更改店家狀態（可選擇多個或全部） - `/api/vendors/status/bulk` - `PUT` 請求 + `{
	 * "vendorIds": [1, 2, 3], "status": true/false }` - 如果 `vendorIds` 為空，則更新所有店家
	 */
	@PutMapping("/api/admin/vendors/status/bulk")
	public ResponseEntity<String> bulkUpdateVendorStatus(@RequestBody BulkVendorStatusRequest request) {
		boolean success = managevendorService.bulkUpdateVendorStatus(request.getVendorIds(), request.isStatus());
		return success ? ResponseEntity.ok("批量狀態已更新") : ResponseEntity.status(HttpStatus.BAD_REQUEST).body("更新失敗");
	}

	/** 📌 用來解析 JSON 的請求 */
	public static class VendorStatusRequest {
		private boolean status;

		public boolean isStatus() {
			return status;
		}

		public void setStatus(boolean status) {
			this.status = status;
		}
	}

	public static class BulkVendorStatusRequest {
		private List<Integer> vendorIds;
		private boolean status;

		public List<Integer> getVendorIds() {
			return vendorIds;
		}

		public void setVendorIds(List<Integer> vendorIds) {
			this.vendorIds = vendorIds;
		}

		public boolean isStatus() {
			return status;
		}

		public void setStatus(boolean status) {
			this.status = status;
		}
	}
}
