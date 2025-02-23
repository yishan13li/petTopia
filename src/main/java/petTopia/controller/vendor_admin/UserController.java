package petTopia.controller.vendor_admin;

import java.io.IOException;
import java.util.Base64;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpSession;
import petTopia.model.vendor_admin.User;
import petTopia.model.vendor_admin.UserRole;
import petTopia.model.vendor_admin.VendorCategory;
import petTopia.model.vendor_admin.Vendor;
import petTopia.repository.vendor_admin.VendorCategoryRepository;
import petTopia.service.vendor_admin.UserService;
import petTopia.service.vendor_admin.VendorService;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserService userService;

	@Autowired
	private VendorService vendorService;

	@Autowired
	private VendorCategoryRepository categoryRepository;

	// 進入店家個人資料頁面
//	@GetMapping("/profile")
//	public String getVendorProfile(@RequestParam String email, @RequestParam String password, Model model) {
//		Optional<User> user = userService.getUserByEmailAndPassword(email, password);
//
//		if (user.isPresent() && user.get().getUserRole() == UserRole.vendor) {
//			// 查詢對應的 VendorDetail
//
//			Optional<VendorDetail> vendorDetail = vendorService.getVendorById(user.get().getUserId());
//			if (vendorDetail.isPresent()) {
//				VendorDetail vendor = vendorDetail.get();
//				String vendorLogoImgBase64 = (vendor.getVendorLogoImg() != null)
//                        ? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(vendor.getVendorLogoImg())
//                        : null;
//				model.addAttribute("user", user.get());
//				model.addAttribute("vendor", vendorDetail.get());
//				 model.addAttribute("vendorLogoImgBase64", vendorLogoImgBase64);
//				return "vendor_admin/app-profile"; // 顯示 vendor 的 JSP
//			}
//		}
//		return "error"; // 若找不到，轉向錯誤頁面
//	}

//	// 更新店家資料
//	@PostMapping("/update")
//	public String updateVendor(@RequestParam Integer vendorId, @RequestParam String vendorName,
//			@RequestParam String category, // 前端傳來的類別名稱
//			 @RequestParam String contactPerson,@RequestParam String contactEmail, @RequestParam String vendorPhone, @RequestParam String vendorAddress,@RequestParam String vendorDescription,
//			 @RequestParam String vendorTaxidNumber, @RequestParam(required = false) MultipartFile profileImage, Model model) {
//
//		VendorDetail vendor = vendorService.getVendorById(vendorId)
//				.orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//		// 設定資料
//		vendor.setVendorName(vendorName);
//		vendor.setContactEmail(contactEmail);
//		vendor.setVendorPhone(vendorPhone);
//		vendor.setContactPerson(contactPerson);
//		vendor.setVendorAddress(vendorAddress);
//		vendor.setVendorDescription(vendorDescription);
//		vendor.setVendorTaxidNumber(vendorTaxidNumber);
//
//		
//		// 更新圖片
//        if (profileImage != null && !profileImage.isEmpty()) {
//            try {
//                vendor.setVendorLogoImg(profileImage.getBytes());
//            } catch (IOException e) {
//                e.printStackTrace();
//                model.addAttribute("error", "圖片上傳失敗");
//                return "error"; // 如果圖片上傳失敗，跳到錯誤頁面
//            }
//        }
//		// 透過 `category` 找到對應的 `VendorCategory`
//		VendorCategory vendorCategory = categoryRepository.findByCategoryName(category)
//				.orElseThrow(() -> new RuntimeException("Category not found"));
//		vendor.setCategory(vendorCategory);
//
//		VendorDetail updatedVendor = vendorService.updateVendor(vendor);
//		model.addAttribute("updateSuccess", true);
//		model.addAttribute("vendor", updatedVendor);
//
//		return "vendor_admin/app-profile"; // 更新完後，重新載入頁面
//	}
	
//	// 取得店家圖片 (回傳圖片資料流)
//    @GetMapping("/profileImage/{vendorId}")
//    public ResponseEntity<byte[]> getProfileImage(@PathVariable Integer vendorId) {
//        VendorDetail vendor = vendorService.getVendorById(vendorId)
//                .orElseThrow(() -> new RuntimeException("Vendor not found"));
//
//        byte[] imageBytes = vendor.getVendorLogoImg();
//        if (imageBytes == null || imageBytes.length == 0) {
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
//        }
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", "image/jpeg");
//
//        return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
//    }
//    
//    @PostMapping("/deleteUser.controller")
//    public String deleteUser(@RequestParam Integer userId, HttpSession session, Model model) {
//        try {
//            userService.deleteUserById(userId);
//            session.invalidate(); // 清除 Session，登出
//            return "redirect:/loginsystemmain.controller"; // 刪除後返回登入頁
//        } catch (Exception e) {
//            model.addAttribute("error", "刪除失敗，請稍後再試！");
//            return "vendor_admin/app-profile"; // 刪除失敗則回到原頁面
//        }
//    }
}
