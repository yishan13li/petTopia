package petTopia.controller.vendor_admin;

import java.io.IOException;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import petTopia.model.vendor_admin.User;
import petTopia.model.vendor_admin.UserRole;
import petTopia.model.vendor_admin.Vendor;
import petTopia.model.vendor_admin.VendorCategory;
import petTopia.repository.vendor_admin.VendorCategoryRepository;
import petTopia.service.vendor_admin.UserService;
import petTopia.service.vendor_admin.VendorServiceImpl;

@Controller

public class VendorProfileController {
	@Autowired
	private VendorServiceImpl vendorServiceImpl;

	@Autowired
	private UserService userService;

	@Autowired
	private VendorCategoryRepository categoryRepository;

	// 根據用戶的 email 和 password 獲取 Vendor Profile
	@GetMapping("/vendor/profile")
	public String getVendorProfile(@RequestParam String email, @RequestParam String password, Model model) {
		Optional<User> user = userService.getUserByEmailAndPassword(email, password);

		System.out.println(user.get().getUserId());
		if (user.isPresent() && user.get().getUserRole() == UserRole.vendor) {
			Optional<Vendor> vendorDetail = vendorServiceImpl.getVendorById(user.get().getUserId());
			List<VendorCategory> allcategory = categoryRepository.findAll();
			if (vendorDetail.isPresent()) {
				System.out.println(vendorDetail.get().getId());
				System.out.println(vendorDetail.get().getName());
				Vendor vendor = vendorDetail.get();
				String vendorLogoImgBase64 = (vendor.getLogoImg() != null)
						? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(vendor.getLogoImg())
						: null;
				model.addAttribute("allcategory", allcategory);
				model.addAttribute("user", user.get());
				model.addAttribute("vendor", vendorDetail.get());
				model.addAttribute("vendorLogoImgBase64", vendorLogoImgBase64);
				return "vendor_admin/vendor_admin_profile";
			}
		}
		return "error"; // 返回錯誤頁面
	}

	// 根據登入用戶的 ID 獲取對應的商家(登入後)
//	    Long loggedInUserId = getLoggedInUserId();  // 獲取目前登入用戶的 ID
//	    Vendor vendor = vendorService.getVendorByUserId(loggedInUserId)
//	            .orElseThrow(() -> new RuntimeException("Vendor not found"));
	// 更新商家資料

	@ResponseBody
	@PostMapping("/api/vendor/update/{vendorId}")
	public ResponseEntity<Map<String, Object>> updateVendor(@PathVariable Integer vendorId,
			@RequestParam(required = false) String vendorName, @RequestParam(required = false) String contactEmail,
			@RequestParam(required = false) String vendorPhone, @RequestParam(required = false) String vendorAddress,
			@RequestParam(required = false) String vendorDescription,
			@RequestParam(required = false) String contactPerson,
			@RequestParam(required = false) String vendorTaxidNumber, @RequestParam(required = false) String category,
			@RequestParam(required = false) MultipartFile vendorLogoImg, Model model) {
		Map<String, Object> response = new HashMap<>();
		// 查找原本的商家資料
		Vendor vendor = vendorServiceImpl.getVendorById(vendorId)
				.orElseThrow(() -> new RuntimeException("Vendor not found"));

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
				e.printStackTrace();
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
						.body(Collections.singletonMap("error", "Image upload failed"));
			}
		}

		// 只有在傳遞 category 時才更新它
		if (category != null) {
			VendorCategory vendorCategory = categoryRepository.findByCategoryName(category)
					.orElseThrow(() -> new RuntimeException("Category not found"));
			vendor.setVendorCategory(vendorCategory);
		}

		// 保存更新過的資料
		Vendor updatedVendor = vendorServiceImpl.updateVendor(vendor);

		response.put("success", true);
		response.put("vendor", updatedVendor);

		return ResponseEntity.ok(response);
	}

	// 取得店家圖片 (回傳圖片資料流)
	@GetMapping("/profileImage/{vendorId}")
	public ResponseEntity<byte[]> getProfileImage(@PathVariable Integer vendorId) {
		Vendor vendor = vendorServiceImpl.getVendorById(vendorId)
				.orElseThrow(() -> new RuntimeException("Vendor not found"));

		byte[] imageBytes = vendor.getLogoImg();
		if (imageBytes == null || imageBytes.length == 0) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.set("Content-Type", "image/jpeg");

		return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
	}

//	@PostMapping("/deleteUser.controller")
//	public String deleteUser(@RequestParam Integer userId, HttpSession session, Model model) {
//		try {
//			userService.deleteUserById(userId);
//			session.invalidate(); // 清除 Session，登出
//			return "redirect:/loginsystemmain.controller"; // 刪除後返回登入頁
//		} catch (Exception e) {
//			model.addAttribute("error", "刪除失敗，請稍後再試！");
//			return "vendor_admin/app-profile"; // 刪除失敗則回到原頁面
//		}
//	}
}
