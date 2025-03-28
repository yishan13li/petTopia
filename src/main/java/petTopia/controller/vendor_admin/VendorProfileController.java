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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorActivityImages;
import petTopia.model.vendor.VendorCategory;
import petTopia.model.vendor.VendorImages;
import petTopia.repository.vendor.VendorCategoryRepository;
import petTopia.repository.vendor.VendorImagesRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.service.vendor_admin.VendorServiceAdmin;

@Controller

public class VendorProfileController {
	@Autowired
	private VendorServiceAdmin vendorService;

	@Autowired
	private VendorRepository vendorRepository;

//	@Autowired
//	private UserService userService;

	@Autowired
	private VendorCategoryRepository categoryRepository;

	@Autowired
	private VendorImagesRepository vendorImagesRepository;

	// 根據用戶的 email 和 password 獲取 Vendor Profile
	@GetMapping("/vendor/profile")
	public String getVendorProfile(@RequestParam String email, @RequestParam String password, Model model) {
//		Optional<User> user = userService.getUserByEmailAndPassword(email, password);

//		Optional<Vendor> vendorDetail = vendorService.getVendorProfile(email, password);
//
//		if (vendorDetail.isPresent()) {
//			Vendor vendor = vendorDetail.get();
//			String vendorLogoImgBase64 = vendorService.getVendorLogoBase64(vendor);
//			List<VendorCategory> allcategory = vendorService.getAllVendorCategories();
//
//			// 获取该店家的活动总数
//			int activityCount = vendorService.getActivityCountByVendor(vendor.getId());
//
//			model.addAttribute("allcategory", allcategory);
//			model.addAttribute("vendor", vendor);
//			model.addAttribute("vendorLogoImgBase64", vendorLogoImgBase64);
//			return "vendor_admin/vendor_admin_profile";
//		}
		
		

//		System.out.println(user.get().getUserId());
//		if (user.isPresent() && user.get().getUserRole() == UserRole.vendor) {
//			Optional<Vendor> vendorDetail = vendorServiceImpl.getVendorById(user.get().getUserId());
//			List<VendorCategory> allcategory = categoryRepository.findAll();
//			if (vendorDetail.isPresent()) {
//				System.out.println(vendorDetail.get().getId());
//				System.out.println(vendorDetail.get().getName());
//				Vendor vendor = vendorDetail.get();
//				String vendorLogoImgBase64 = (vendor.getLogoImg() != null)
//						? "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(vendor.getLogoImg())
//						: null;
//				model.addAttribute("allcategory", allcategory);
//				model.addAttribute("user", user.get());
//				model.addAttribute("vendor", vendorDetail.get());
//				model.addAttribute("vendorLogoImgBase64", vendorLogoImgBase64);
//				return "vendor_admin/vendor_admin_profile";
//			}
//		}
		return "error"; // 返回錯誤頁面
	}
	
	@GetMapping("api/vendor_admin/status/{vendorId}")
    public ResponseEntity<?> getVendorStatus(@PathVariable Integer vendorId) {
        Optional<Vendor> statusOptional = vendorService.getVendorStatus(vendorId);

        if (statusOptional.isPresent()) {
            return ResponseEntity.ok(Collections.singletonMap("status", statusOptional.get().isStatus()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Collections.singletonMap("error", "找不到該店家"));
        }
    }
	
	@ResponseBody
	@GetMapping("api/vendor_admin/profile/{id}")
    public ResponseEntity<?> getVendorById(@PathVariable Integer id) {
        Optional<Vendor> vendor = vendorRepository.findById(id);
        return ResponseEntity.ok(Map.of("vendor", vendor));
    }
	
	@ResponseBody
	@GetMapping("api/vendor_admin/profile")
	public ResponseEntity<Map<String, Object>> getVendorProfile(@RequestParam Integer id) {
//	    Optional<Vendor> vendorDetail = vendorService.getVendorProfile(email, password);
	    Optional<Vendor> vendorDetail = vendorRepository.findById(id);
	    Map<String, Object> response = new HashMap<>();
	    
	    if (vendorDetail.isPresent()) {
	        Vendor vendor = vendorDetail.get();
	        String vendorLogoImgBase64 = vendorService.getVendorLogoBase64(vendor);
	        List<VendorCategory> allcategory = vendorService.getAllVendorCategories();

	        // 获取该店家的活动总数
	        int activityCount = vendorService.getActivityCountByVendor(vendor.getId());

	        // 將資料組成 Map 回傳
	        response.put("vendor", vendor);
	        response.put("allcategory", allcategory);
	        response.put("vendorLogoImgBase64", vendorLogoImgBase64);

	        return ResponseEntity.ok(response);
	    } else {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
	    }
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
			@RequestParam(required = false) String vendorTaxidNumber, @RequestParam(required = false) Integer category,
			@RequestParam(required = false) MultipartFile vendorLogoImg,@RequestParam(value = "files", required = false) MultipartFile[] files,
			@RequestParam(value = "deletedImageIds", required = false) List<Integer> deletedImageIds, Model model) throws IOException {
		Map<String, Object> response = new HashMap<>();
		// 查找原本的商家資料
		Vendor vendor = vendorService.getVendorById(vendorId)
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
			VendorCategory vendorCategory = categoryRepository.findById(category)
					.orElseThrow(() -> new RuntimeException("Category not found"));
			vendor.setVendorCategory(vendorCategory);
		}

		// 保存更新過的資料
		Vendor updatedVendor = vendorService.updateVendor(vendor);

		
		if (deletedImageIds != null && !deletedImageIds.isEmpty()) {
			vendorImagesRepository.deleteAllById(deletedImageIds);
		}

		// 4. 更新新圖片（如果有新圖片則更新）
		if (files != null && files.length > 0) {
			List<VendorImages> vendorImagesList = new ArrayList<>();
			for (MultipartFile file : files) {
				if (!file.isEmpty()) {
					System.out.println("上傳的圖片：" + file.getOriginalFilename());
					VendorImages vendorImage = new VendorImages();
					vendorImage.setImage(file.getBytes());
					vendorImage.setVendor(vendor);
					vendorImagesList.add(vendorImage);
					System.out.println(vendorImagesList);
				}
			}
			vendor.getVendorImages().addAll(vendorImagesList);
			vendorImagesRepository.saveAll(vendorImagesList);
		}
		response.put("success", true);
		response.put("vendor", updatedVendor);

		return ResponseEntity.ok(response);
	}

	// 取得店家圖片 (回傳圖片資料流)
	@GetMapping("/profileImage/{vendorId}")
	public ResponseEntity<byte[]> getProfileImage(@PathVariable Integer vendorId) {
		Vendor vendor = vendorService.getVendorById(vendorId)
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

	@GetMapping("/profile_photos/download")
	public ResponseEntity<?> downloadPhotoById(@RequestParam Integer photoId) {
		Optional<VendorImages> imageOpt = vendorImagesRepository.findById(photoId);

		if (imageOpt.isPresent()) {
			VendorImages image = imageOpt.get();
			byte[] imageFile = image.getImage(); // 假設每個 VendorActivityPhoto 實體有一個 photoFile 字段，存儲圖片二進制數據

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG); // 假設圖片是 JPEG 格式

			return new ResponseEntity<>(imageFile, headers, HttpStatus.OK); // 返回圖片的二進制數據
		}

		return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 如果找不到圖片，返回 404
	}

	@GetMapping("/profile_photos/ids")
	public ResponseEntity<List<Integer>> findPhotoIdsByVendorId(@RequestParam Integer vendorId) {
	    Optional<Vendor> op = vendorRepository.findById(vendorId);

	    List<Integer> imageIdList = new ArrayList<>();

	    if (op.isPresent()) {
	        Vendor vendor = op.get();
	        List<VendorImages> images = vendor.getVendorImages(); // 获取店家的所有图片

	        for (VendorImages image : images) {
	            imageIdList.add(image.getId()); // 获取图片的 ID
	        }

	        return new ResponseEntity<>(imageIdList, HttpStatus.OK); // 返回所有照片的 ID 列表
	    }

	    return new ResponseEntity<>(HttpStatus.NOT_FOUND); // 如果沒有找到店家，返回 404
	}
	
	// 根据vendorId获取该店铺的所有标语
	@ResponseBody
	@GetMapping("/api/vendor/{vendorId}/slogans")
	public List<String> getCertifiedSlogansByVendorId(@PathVariable Integer vendorId) {
	    return vendorService.getSlogansByVendorId(vendorId);
	}

}
