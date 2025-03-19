package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import petTopia.jwt.JwtUtil;
import petTopia.model.user.Users;
import petTopia.service.user.VendorRegistrationService;

import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/vendor")
public class VendorConversionController {
    
    private static final Logger logger = LoggerFactory.getLogger(VendorConversionController.class);
    
    @Autowired
    private VendorRegistrationService vendorRegistrationService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 會員轉換為商家
     */
    @PostMapping("/convert")
    public ResponseEntity<?> convertToVendor(
            @RequestBody Map<String, Boolean> request,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        Boolean confirm = request.get("confirm");
        
        logger.info("處理會員轉換為商家請求 - 確認狀態: {}", confirm);
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "請先登入會員帳號"));
        }

        String token = authHeader.substring(7);
        try {
            String email = jwtUtil.extractUsername(token);
            if (!jwtUtil.validateToken(token, email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "無效的令牌"));
            }

            String role = jwtUtil.extractUserRole(token);
            if (!"MEMBER".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "只有會員可以轉換為商家"));
            }

            Integer userId = jwtUtil.extractUserId(token);
            
            // 檢查是否已有商家帳號
            Users existingVendor = vendorRegistrationService.findVendorByEmail(email);
            
            if (existingVendor != null) {
                logger.info("用戶已有商家帳號，執行切換 - 用戶ID: {}, 電子郵件: {}", 
                    userId, email);
                
                // 檢查是否為第三方登入用戶
                if (existingVendor.getProvider() != Users.Provider.LOCAL || 
                    existingVendor.getPassword() == null || 
                    existingVendor.getPassword().isEmpty()) {
                    
                    logger.info("第三方登入用戶，跳過密碼認證，直接生成新的JWT");
                    
                    // 生成新的 JWT，不需要重新認證
                    String newToken = jwtUtil.generateToken(
                        existingVendor.getEmail(), 
                        existingVendor.getId(), 
                        existingVendor.getUserRole().toString()
                    );
                    
                    Map<String, Object> result = new HashMap<>();
                    result.put("message", "已切換至商家帳號");
                    result.put("token", newToken);
                    result.put("vendorId", existingVendor.getId());
                    result.put("email", existingVendor.getEmail());
                    result.put("role", existingVendor.getUserRole().toString());
                    
                    return ResponseEntity.ok(result);
                } else {
                    // 如果是本地帳號且有密碼，嘗試正常認證
                    try {
                        // 創建新的認證令牌
                        Authentication authentication = authenticationManager.authenticate(
                            new UsernamePasswordAuthenticationToken(email, existingVendor.getPassword())
                        );
                        
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        
                        // 生成新的 JWT
                        String newToken = jwtUtil.generateToken(existingVendor.getEmail(), existingVendor.getId(), existingVendor.getUserRole().toString());
                        
                        Map<String, Object> result = new HashMap<>();
                        result.put("message", "已切換至商家帳號");
                        result.put("token", newToken);
                        result.put("vendorId", existingVendor.getId());
                        result.put("email", existingVendor.getEmail());
                        result.put("role", existingVendor.getUserRole().toString());
                        
                        return ResponseEntity.ok(result);
                    } catch (Exception e) {
                        logger.warn("嘗試認證失敗，可能是密碼問題，改為直接生成新JWT", e);
                        
                        // 認證失敗時，也直接生成新的JWT
                        String newToken = jwtUtil.generateToken(
                            existingVendor.getEmail(), 
                            existingVendor.getId(), 
                            existingVendor.getUserRole().toString()
                        );
                        
                        Map<String, Object> result = new HashMap<>();
                        result.put("message", "已切換至商家帳號");
                        result.put("token", newToken);
                        result.put("vendorId", existingVendor.getId());
                        result.put("email", existingVendor.getEmail());
                        result.put("role", existingVendor.getUserRole().toString());
                        
                        return ResponseEntity.ok(result);
                    }
                }
            }
            
            // 如果沒有商家帳號且未確認要註冊
            if (confirm == null || !confirm) {
                logger.info("用戶需要確認轉換為商家 - 用戶ID: {}", userId);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "需要確認轉換",
                        "needConfirm", true,
                        "message", "轉換為商家帳號將創建一個新的商家帳號，請確認是否繼續？"
                    ));
            }
            
            // 確認要註冊新商家帳號
            logger.info("開始轉換會員為商家 - 用戶ID: {}", userId);
            Map<String, Object> conversionResult = vendorRegistrationService.convertMemberToVendor(userId);
            
            if ((Boolean) conversionResult.get("success")) {
                Users newVendor = (Users) conversionResult.get("vendorUser");
                logger.info("會員轉換為商家成功 - 會員ID: {}, 新商家ID: {}", 
                    userId, newVendor.getId());
                
                // 生成新的 JWT，不需要重新認證
                String newToken = jwtUtil.generateToken(
                    newVendor.getEmail(), 
                    newVendor.getId(), 
                    newVendor.getUserRole().toString()
                );
                
                Map<String, Object> response = new HashMap<>();
                response.put("message", "商家轉換成功");
                response.put("token", newToken);
                response.put("vendorId", newVendor.getId());
                response.put("email", newVendor.getEmail());
                response.put("role", newVendor.getUserRole().toString());
                
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else {
                logger.warn("會員轉換為商家失敗 - 用戶ID: {}, 原因: {}", 
                    userId, conversionResult.get("message"));
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", conversionResult.get("message")));
            }
            
        } catch (Exception e) {
            logger.error("商家轉換過程發生異常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "商家轉換失敗：" + e.getMessage()));
        }
    }
    
    /**
     * 檢查當前用戶是否可以轉換為商家
     */
    @GetMapping("/convert/check")
    public ResponseEntity<?> checkConversionEligibility(
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        logger.info("檢查用戶是否可以轉換為商家");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "請先登入"));
        }

        String token = authHeader.substring(7);
        try {
            String email = jwtUtil.extractUsername(token);
            if (!jwtUtil.validateToken(token, email)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "無效的令牌"));
            }

            String role = jwtUtil.extractUserRole(token);
            Integer userId = jwtUtil.extractUserId(token);
            
            Map<String, Object> result = new HashMap<>();
            
            // 如果已經是商家
            if ("VENDOR".equals(role)) {
                logger.info("用戶已經是商家 - 用戶ID: {}", userId);
                result.put("eligible", false);
                result.put("message", "您已經是商家帳號");
                return ResponseEntity.ok(result);
            }
            
            // 檢查是否已有商家帳號
            Users existingVendor = vendorRegistrationService.findVendorByEmail(email);
            
            if (existingVendor != null) {
                logger.info("用戶已有商家帳號 - 用戶ID: {}, 商家ID: {}", 
                    userId, existingVendor.getId());
                result.put("eligible", true);
                result.put("hasExistingAccount", true);
                result.put("message", "您已有商家帳號，可以直接切換");
            } else {
                logger.info("用戶符合商家轉換資格 - 用戶ID: {}", userId);
                result.put("eligible", true);
                result.put("hasExistingAccount", false);
                result.put("message", "您可以轉換為商家帳號");
            }
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            logger.error("檢查商家轉換資格時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "檢查失敗：" + e.getMessage()));
        }
    }
} 