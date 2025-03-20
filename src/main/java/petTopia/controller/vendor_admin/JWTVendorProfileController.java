// package petTopia.controller.vendor_admin;

// import java.io.IOException;
// import java.util.HashMap;
// import java.util.List;
// import java.util.Map;
// import java.util.Optional;
// import java.util.stream.Collectors;

// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.http.HttpHeaders;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

// import petTopia.model.vendor.Vendor;
// import petTopia.model.vendor.VendorCategory;
// import petTopia.model.vendor.VendorImages;
// import petTopia.repository.vendor.VendorCategoryRepository;
// import petTopia.repository.vendor.VendorImagesRepository;
// import petTopia.repository.vendor.VendorRepository;
// import petTopia.service.vendor_admin.VendorServiceAdmin;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

// /**
//  * 商家資料控制器 - 使用 JWT 進行身份驗證
//  * 主要功能：處理商家資料的查詢、更新、圖片上傳等操作
//  */
// @RestController  // 標記為 REST 控制器，直接返回 JSON 數據
// @RequestMapping("/api/vendor-admin")  // 設定基礎 URL 路徑
// @PreAuthorize("hasRole('VENDOR')")  // 確保只有商家角色可以訪問
// public class JWTVendorProfileController {

//     // 初始化日誌記錄器，用於記錄系統運行日誌
//     private static final Logger logger = LoggerFactory.getLogger(JWTVendorProfileController.class);

//     // 注入所需的服務和倉儲
//     @Autowired
//     private VendorRepository vendorRepository;  // 商家資料倉儲，用於數據庫操作

//     @Autowired
//     private VendorCategoryRepository categoryRepository;  // 商家類別倉儲

//     @Autowired
//     private VendorServiceAdmin vendorService;  // 商家服務層，處理業務邏輯

//     @Autowired
//     private VendorImagesRepository vendorImagesRepository;  // 商家圖片倉儲

//     /**
//      * 創建商家資料 DTO
//      * 用於處理序列化問題，避免直接返回實體對象
//      * @param vendor 商家實體
//      * @return 包含商家資料的 Map 對象
//      */
//     private Map<String, Object> createVendorDTO(Vendor vendor) {
//         Map<String, Object> dto = new HashMap<>();
//         // 設置基本資料
//         dto.put("id", vendor.getId());
//         dto.put("name", vendor.getName());
//         dto.put("contactEmail", vendor.getContactEmail());
//         dto.put("phone", vendor.getPhone());
//         dto.put("address", vendor.getAddress());
//         dto.put("description", vendor.getDescription());
//         dto.put("contactPerson", vendor.getContactPerson());
//         dto.put("taxidNumber", vendor.getTaxidNumber());
        
//         // 處理商家類別
//         if (vendor.getVendorCategory() != null) {
//             Map<String, Object> categoryDTO = new HashMap<>();
//             categoryDTO.put("id", vendor.getVendorCategory().getId());
//             categoryDTO.put("name", vendor.getVendorCategory().getName());
//             dto.put("vendorCategory", categoryDTO);
//         } else {
//             dto.put("vendorCategory", null);
//         }
        
//         // 標記是否有 logo 圖片
//         dto.put("logoImg", vendor.getLogoImg() != null);
//         return dto;
//     }

//     /**
//      * 獲取商家資料
//      * 需要 JWT token 驗證
//      * @param vendorId 商家 ID
//      * @return 包含商家資料的 ResponseEntity
//      */
//     @GetMapping("/profile/{vendorId}")
//     public ResponseEntity<Map<String, Object>> getVendorProfile(@PathVariable Integer vendorId) {
//         try {
//             // 驗證當前用戶身份
//             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//             if (authentication == null || !authentication.isAuthenticated()) {
//                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                     .body(Map.of("error", "請先登入"));
//             }

//             // 驗證用戶ID是否匹配
//             Integer currentUserId = Integer.parseInt(authentication.getName());
//             if (!currentUserId.equals(vendorId)) {
//                 return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                     .body(Map.of("error", "無權限訪問此資源"));
//             }

//             // 獲取商家資料
//             Optional<Vendor> vendorDetail = vendorRepository.findById(vendorId);
//             if (vendorDetail.isEmpty()) {
//                 return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                     .body(Map.of("error", "找不到商家資料"));
//             }

//             // 獲取所有商家類別
//             List<VendorCategory> allcategory = categoryRepository.findAll();
//             List<Map<String, Object>> categoryDTOs = allcategory.stream()
//                 .map(category -> {
//                     Map<String, Object> categoryDTO = new HashMap<>();
//                     categoryDTO.put("id", category.getId());
//                     categoryDTO.put("name", category.getName());
//                     return categoryDTO;
//                 })
//                 .collect(Collectors.toList());

//             // 構建回應數據
//             Map<String, Object> response = new HashMap<>();
//             response.put("vendor", createVendorDTO(vendorDetail.get()));
//             response.put("allcategory", categoryDTOs);

//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             // 錯誤處理
//             logger.error("獲取商家資料失敗", e);
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                 .body(Map.of("error", "獲取商家資料失敗：" + e.getMessage()));
//         }
//     }

//     /**
//      * 更新商家資料
//      * 支持更新基本資料和上傳圖片
//      * @param vendorId 商家 ID
//      * @param vendorName 商家名稱
//      * @param contactEmail 聯絡郵箱
//      * @param vendorPhone 聯絡電話
//      * @param vendorAddress 地址
//      * @param vendorDescription 描述
//      * @param contactPerson 聯絡人
//      * @param vendorTaxidNumber 統一編號
//      * @param category 商家類別 ID
//      * @param vendorLogoImg 商家 logo 圖片
//      * @return 更新結果的 ResponseEntity
//      */
//     @PutMapping("/profile/{vendorId}")
//     public ResponseEntity<Map<String, Object>> updateVendorProfile(
//             @PathVariable Integer vendorId,
//             @RequestParam(required = false) String vendorName,
//             @RequestParam(required = false) String contactEmail,
//             @RequestParam(required = false) String vendorPhone,
//             @RequestParam(required = false) String vendorAddress,
//             @RequestParam(required = false) String vendorDescription,
//             @RequestParam(required = false) String contactPerson,
//             @RequestParam(required = false) String vendorTaxidNumber,
//             @RequestParam(required = false) Integer category,
//             @RequestParam(required = false) MultipartFile vendorLogoImg) {
        
//         try {
//             // 驗證當前用戶身份
//             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//             if (authentication == null || !authentication.isAuthenticated()) {
//                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                     .body(Map.of("error", "請先登入"));
//             }

//             // 驗證用戶ID是否匹配
//             Integer currentUserId = Integer.parseInt(authentication.getName());
//             if (!currentUserId.equals(vendorId)) {
//                 return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                     .body(Map.of("error", "無權限訪問此資源"));
//             }

//             // 獲取並更新商家資料
//             Vendor vendor = vendorService.getVendorById(vendorId)
//                     .orElseThrow(() -> new RuntimeException("找不到商家資料"));

//             // 更新基本資料
//             if (vendorName != null) vendor.setName(vendorName);
//             if (contactEmail != null) vendor.setContactEmail(contactEmail);
//             if (vendorPhone != null) vendor.setPhone(vendorPhone);
//             if (contactPerson != null) vendor.setContactPerson(contactPerson);
//             if (vendorAddress != null) vendor.setAddress(vendorAddress);
//             if (vendorDescription != null) vendor.setDescription(vendorDescription);
//             if (vendorTaxidNumber != null) vendor.setTaxidNumber(vendorTaxidNumber);

//             // 處理圖片上傳
//             if (vendorLogoImg != null && !vendorLogoImg.isEmpty()) {
//                 try {
//                     vendor.setLogoImg(vendorLogoImg.getBytes());
//                 } catch (IOException e) {
//                     logger.error("圖片上傳失敗", e);
//                     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                             .body(Map.of("error", "圖片上傳失敗"));
//                 }
//             }

//             // 更新商家類別
//             if (category != null) {
//                 VendorCategory vendorCategory = categoryRepository.findById(category)
//                         .orElseThrow(() -> new RuntimeException("找不到商家類別"));
//                 vendor.setVendorCategory(vendorCategory);
//             }

//             // 保存更新
//             Vendor updatedVendor = vendorService.updateVendor(vendor);

//             Map<String, Object> response = new HashMap<>();
//             response.put("success", true);
//             response.put("vendor", createVendorDTO(updatedVendor));

//             return ResponseEntity.ok(response);
//         } catch (Exception e) {
//             // 錯誤處理
//             logger.error("更新商家資料失敗", e);
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                 .body(Map.of("error", "更新商家資料失敗：" + e.getMessage()));
//         }
//     }

//     /**
//      * 獲取商家 logo 圖片
//      * @param vendorId 商家 ID
//      * @return 包含圖片的 ResponseEntity
//      */
//     @GetMapping("/profile/{vendorId}/logo")
//     public ResponseEntity<?> getProfileImage(@PathVariable Integer vendorId) {
//         try {
//             // 驗證當前用戶身份
//             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//             if (authentication == null || !authentication.isAuthenticated()) {
//                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                     .body(Map.of("error", "請先登入"));
//             }

//             // 驗證用戶ID是否匹配
//             Integer currentUserId = Integer.parseInt(authentication.getName());
//             if (!currentUserId.equals(vendorId)) {
//                 return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                     .body(Map.of("error", "無權限訪問此資源"));
//             }

//             // 獲取商家資料
//             Vendor vendor = vendorService.getVendorById(vendorId)
//                     .orElseThrow(() -> new RuntimeException("找不到商家資料"));

//             // 獲取並返回圖片
//             byte[] imageBytes = vendor.getLogoImg();
//             if (imageBytes == null || imageBytes.length == 0) {
//                 return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                     .body(Map.of("error", "找不到商家圖片"));
//             }

//             // 設置響應頭並返回圖片
//             HttpHeaders headers = new HttpHeaders();
//             headers.setContentType(MediaType.IMAGE_JPEG);
//             return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
//         } catch (Exception e) {
//             // 錯誤處理
//             logger.error("獲取商家圖片失敗", e);
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                 .body(Map.of("error", "獲取商家圖片失敗：" + e.getMessage()));
//         }
//     }

//     /**
//      * 獲取商家相冊圖片 ID 列表
//      */
//     @GetMapping("/profile/photos/ids")
//     public ResponseEntity<?> getPhotoIds() {
//         try {
//             // 驗證當前用戶身份
//             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//             if (authentication == null || !authentication.isAuthenticated()) {
//                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                     .body(Map.of("error", "請先登入"));
//             }

//             // 獲取當前用戶ID
//             Integer userId = Integer.parseInt(authentication.getName());

//             // 獲取商家資料
//             Optional<Vendor> vendorOpt = vendorRepository.findById(userId);
//             if (vendorOpt.isEmpty()) {
//                 return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                     .body(Map.of("error", "找不到商家資料"));
//             }

//             // 獲取並返回圖片 ID 列表
//             Vendor vendor = vendorOpt.get();
//             List<Integer> imageIdList = vendor.getVendorImages().stream()
//                     .map(VendorImages::getId)
//                     .toList();

//             return ResponseEntity.ok(imageIdList);
//         } catch (Exception e) {
//             // 錯誤處理
//             logger.error("獲取商家相冊圖片 ID 列表失敗", e);
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                 .body(Map.of("error", "獲取商家相冊圖片 ID 列表失敗：" + e.getMessage()));
//         }
//     }

//     /**
//      * 下載相冊圖片
//      */
//     @GetMapping("/profile/photos/{photoId}")
//     public ResponseEntity<?> downloadPhoto(@PathVariable Integer photoId) {
//         try {
//             // 驗證當前用戶身份
//             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//             if (authentication == null || !authentication.isAuthenticated()) {
//                 return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                     .body(Map.of("error", "請先登入"));
//             }

//             // 獲取當前用戶ID
//             Integer userId = Integer.parseInt(authentication.getName());

//             // 獲取圖片資料
//             Optional<VendorImages> imageOpt = vendorImagesRepository.findById(photoId);
//             if (imageOpt.isEmpty()) {
//                 return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                     .body(Map.of("error", "找不到圖片"));
//             }

//             // 驗證圖片所有權
//             VendorImages image = imageOpt.get();
//             if (!image.getVendor().getId().equals(userId)) {
//                 return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                     .body(Map.of("error", "沒有權限訪問此圖片"));
//             }

//             // 返回圖片
//             HttpHeaders headers = new HttpHeaders();
//             headers.setContentType(MediaType.IMAGE_JPEG);
//             return new ResponseEntity<>(image.getImage(), headers, HttpStatus.OK);
//         } catch (Exception e) {
//             // 錯誤處理
//             logger.error("下載相冊圖片失敗", e);
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                 .body(Map.of("error", "下載相冊圖片失敗：" + e.getMessage()));
//         }
//     }
// } 