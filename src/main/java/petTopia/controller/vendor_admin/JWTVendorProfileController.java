package petTopia.controller.vendor_admin;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import petTopia.jwt.JwtUtil;
import petTopia.model.vendor.Vendor;
import petTopia.model.vendor.VendorCategory;
import petTopia.model.vendor.VendorImages;
import petTopia.repository.vendor.VendorCategoryRepository;
import petTopia.repository.vendor.VendorImagesRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.service.vendor_admin.VendorServiceAdmin;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 商家資料控制器 - 使用 JWT 進行身份驗證
 * 主要功能：處理商家資料的查詢、更新、圖片上傳等操作
 */
@RestController  // 標記為 REST 控制器，直接返回 JSON 數據
@RequestMapping("/api/vendor")  // 設定基礎 URL 路徑
@PreAuthorize("hasRole('VENDOR')")  // 確保只有商家角色可以訪問
public class JWTVendorProfileController {

    // 初始化日誌記錄器
    private static final Logger logger = LoggerFactory.getLogger(JWTVendorProfileController.class);

    // 注入所需的服務和倉儲
    @Autowired
    private VendorRepository vendorRepository;  // 商家資料倉儲

    @Autowired
    private VendorCategoryRepository categoryRepository;  // 商家類別倉儲

    @Autowired
    private VendorServiceAdmin vendorService;  // 商家服務

    @Autowired
    private JwtUtil jwtUtil;  // JWT 工具類

    @Autowired
    private VendorImagesRepository vendorImagesRepository;  // 商家圖片倉儲

    /**
     * 創建商家資料 DTO
     * 用於處理序列化問題，避免直接返回實體對象
     */
    private Map<String, Object> createVendorDTO(Vendor vendor) {
        Map<String, Object> dto = new HashMap<>();
        // 設置基本資料
        dto.put("id", vendor.getId());
        dto.put("name", vendor.getName());
        dto.put("contactEmail", vendor.getContactEmail());
        dto.put("phone", vendor.getPhone());
        dto.put("address", vendor.getAddress());
        dto.put("description", vendor.getDescription());
        dto.put("contactPerson", vendor.getContactPerson());
        dto.put("taxidNumber", vendor.getTaxidNumber());
        
        // 處理商家類別
        if (vendor.getVendorCategory() != null) {
            Map<String, Object> categoryDTO = new HashMap<>();
            categoryDTO.put("id", vendor.getVendorCategory().getId());
            categoryDTO.put("name", vendor.getVendorCategory().getName());
            dto.put("vendorCategory", categoryDTO);
        } else {
            dto.put("vendorCategory", null);
        }
        
        // 標記是否有 logo 圖片
        dto.put("logoImg", vendor.getLogoImg() != null);
        return dto;
    }

    /**
     * 獲取商家資料
     * 需要 JWT token 驗證
     */
    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getVendorProfile(HttpServletRequest request) {
        try {
            // 1. 驗證 token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }

            String token = authHeader.substring(7);
            
            // 2. 檢查 token 是否過期
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "登入已過期，請重新登入"));
            }

            // 3. 從 token 中獲取用戶 ID
            Integer userId = jwtUtil.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "無效的令牌"));
            }

            // 4. 獲取商家資料
            Optional<Vendor> vendorDetail = vendorRepository.findById(userId);
            if (vendorDetail.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "找不到商家資料"));
            }

            // 5. 獲取所有商家類別
            List<VendorCategory> allcategory = categoryRepository.findAll();
            List<Map<String, Object>> categoryDTOs = allcategory.stream()
                .map(category -> {
                    Map<String, Object> categoryDTO = new HashMap<>();
                    categoryDTO.put("id", category.getId());
                    categoryDTO.put("name", category.getName());
                    return categoryDTO;
                })
                .collect(Collectors.toList());

            // 6. 構建回應
            Map<String, Object> response = new HashMap<>();
            response.put("vendor", createVendorDTO(vendorDetail.get()));
            response.put("allcategory", categoryDTOs);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 7. 錯誤處理
            logger.error("獲取商家資料失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲取商家資料失敗：" + e.getMessage()));
        }
    }

    /**
     * 更新商家資料
     * 支持更新基本資料和上傳圖片
     */
    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateVendorProfile(
            HttpServletRequest request,
            @RequestParam(required = false) String vendorName,
            @RequestParam(required = false) String contactEmail,
            @RequestParam(required = false) String vendorPhone,
            @RequestParam(required = false) String vendorAddress,
            @RequestParam(required = false) String vendorDescription,
            @RequestParam(required = false) String contactPerson,
            @RequestParam(required = false) String vendorTaxidNumber,
            @RequestParam(required = false) Integer category,
            @RequestParam(required = false) MultipartFile vendorLogoImg) {
        
        try {
            // 1. 驗證 token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }

            String token = authHeader.substring(7);
            
            // 2. 檢查 token 是否過期
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "登入已過期，請重新登入"));
            }

            // 3. 從 token 中獲取用戶 ID
            Integer userId = jwtUtil.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "無效的令牌"));
            }

            // 4. 獲取並更新商家資料
            Vendor vendor = vendorService.getVendorById(userId)
                    .orElseThrow(() -> new RuntimeException("找不到商家資料"));

            // 5. 更新基本資料
            if (vendorName != null) vendor.setName(vendorName);
            if (contactEmail != null) vendor.setContactEmail(contactEmail);
            if (vendorPhone != null) vendor.setPhone(vendorPhone);
            if (contactPerson != null) vendor.setContactPerson(contactPerson);
            if (vendorAddress != null) vendor.setAddress(vendorAddress);
            if (vendorDescription != null) vendor.setDescription(vendorDescription);
            if (vendorTaxidNumber != null) vendor.setTaxidNumber(vendorTaxidNumber);

            // 6. 處理圖片上傳
            if (vendorLogoImg != null && !vendorLogoImg.isEmpty()) {
                try {
                    vendor.setLogoImg(vendorLogoImg.getBytes());
                } catch (IOException e) {
                    logger.error("圖片上傳失敗", e);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                            .body(Map.of("error", "圖片上傳失敗"));
                }
            }

            // 7. 更新商家類別
            if (category != null) {
                VendorCategory vendorCategory = categoryRepository.findById(category)
                        .orElseThrow(() -> new RuntimeException("找不到商家類別"));
                vendor.setVendorCategory(vendorCategory);
            }

            // 8. 保存更新
            Vendor updatedVendor = vendorService.updateVendor(vendor);

            // 9. 返回更新結果
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("vendor", createVendorDTO(updatedVendor));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 10. 錯誤處理
            logger.error("更新商家資料失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "更新商家資料失敗：" + e.getMessage()));
        }
    }

    /**
     * 獲取商家 logo 圖片
     */
    @GetMapping("/profile/image")
    public ResponseEntity<?> getProfileImage(HttpServletRequest request) {
        try {
            // 1. 驗證 token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }

            String token = authHeader.substring(7);
            
            // 2. 檢查 token 是否過期
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "登入已過期，請重新登入"));
            }

            // 3. 從 token 中獲取用戶 ID
            Integer userId = jwtUtil.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "無效的令牌"));
            }

            // 4. 獲取商家資料
            Vendor vendor = vendorService.getVendorById(userId)
                    .orElseThrow(() -> new RuntimeException("找不到商家資料"));

            // 5. 獲取並返回圖片
            byte[] imageBytes = vendor.getLogoImg();
            if (imageBytes == null || imageBytes.length == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "找不到商家圖片"));
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);

            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            // 6. 錯誤處理
            logger.error("獲取商家圖片失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲取商家圖片失敗：" + e.getMessage()));
        }
    }

    /**
     * 獲取商家相冊圖片 ID 列表
     */
    @GetMapping("/profile/photos/ids")
    public ResponseEntity<?> getPhotoIds(HttpServletRequest request) {
        try {
            // 1. 驗證 token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }

            String token = authHeader.substring(7);
            
            // 2. 檢查 token 是否過期
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "登入已過期，請重新登入"));
            }

            // 3. 從 token 中獲取用戶 ID
            Integer userId = jwtUtil.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "無效的令牌"));
            }

            // 4. 獲取商家資料
            Optional<Vendor> vendorOpt = vendorRepository.findById(userId);
            if (vendorOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "找不到商家資料"));
            }

            // 5. 獲取並返回圖片 ID 列表
            Vendor vendor = vendorOpt.get();
            List<Integer> imageIdList = vendor.getImages().stream()
                    .map(VendorImages::getId)
                    .toList();

            return ResponseEntity.ok(imageIdList);
        } catch (Exception e) {
            // 6. 錯誤處理
            logger.error("獲取商家相冊圖片 ID 列表失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲取商家相冊圖片 ID 列表失敗：" + e.getMessage()));
        }
    }

    /**
     * 下載相冊圖片
     */
    @GetMapping("/profile/photos/{photoId}")
    public ResponseEntity<?> downloadPhoto(HttpServletRequest request, @PathVariable Integer photoId) {
        try {
            // 1. 驗證 token
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }

            String token = authHeader.substring(7);
            
            // 2. 檢查 token 是否過期
            if (jwtUtil.isTokenExpired(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "登入已過期，請重新登入"));
            }

            // 3. 從 token 中獲取用戶 ID
            Integer userId = jwtUtil.extractUserId(token);
            if (userId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "無效的令牌"));
            }

            // 4. 獲取圖片資料
            Optional<VendorImages> imageOpt = vendorImagesRepository.findById(photoId);
            if (imageOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "找不到圖片"));
            }

            // 5. 驗證圖片所有權
            VendorImages image = imageOpt.get();
            if (!image.getVendor().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "沒有權限訪問此圖片"));
            }

            // 6. 返回圖片
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(image.getImage(), headers, HttpStatus.OK);
        } catch (Exception e) {
            // 7. 錯誤處理
            logger.error("下載相冊圖片失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "下載相冊圖片失敗：" + e.getMessage()));
        }
    }
} 