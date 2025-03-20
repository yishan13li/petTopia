package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import petTopia.model.user.User;
import petTopia.service.user.VendorRegistrationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@RestController
@RequestMapping("/api/vendor/auth")
public class VendorRegisterController {
    
    private static final Logger logger = LoggerFactory.getLogger(VendorRegisterController.class);

    @Autowired
    private VendorRegistrationService vendorRegistrationService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String confirmPassword = request.get("confirmPassword");
        
        logger.info("處理商家註冊請求 - 電子郵件: {}", email);
        
        // 基本驗證
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "電子郵件不能為空"));
        }
        
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "密碼不能為空"));
        }
        
        if (!password.equals(confirmPassword)) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "密碼與確認密碼不符"));
        }

        try {
            // 檢查是否已存在相同email的商家帳號
            User existingVendor = vendorRegistrationService.findByEmail(email);
            if (existingVendor != null) {
                logger.warn("註冊失敗 - 電子郵件已存在: {}", email);
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "此 email 已註冊為商家"));
            }

            // 創建用戶基本信息
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setUserRole(User.UserRole.VENDOR);
            newUser.setProvider(User.Provider.LOCAL);

            // 使用註冊服務處理註冊
            vendorRegistrationService.register(newUser);
            
            logger.info("商家註冊成功 - 電子郵件: {}", email);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "message", "註冊成功，請查收驗證郵件後登入",
                    "email", email
                ));
            
        } catch (Exception e) {
            logger.error("註冊過程發生異常 - 電子郵件: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "註冊失敗：" + e.getMessage()));
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam String token) {
        logger.info("處理商家電子郵件驗證請求 - 令牌: {}", token);
        
        if (token == null || token.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "驗證令牌不能為空"));
        }
        
        try {
            boolean verified = vendorRegistrationService.verifyEmail(token);
            
            if (verified) {
                logger.info("商家電子郵件驗證成功 - 令牌: {}", token);
                return ResponseEntity.ok(Map.of(
                    "message", "驗證成功，請登入",
                    "verified", true
                ));
            } else {
                logger.warn("商家電子郵件驗證失敗 - 令牌: {}", token);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "error", "驗證失敗，請重新註冊",
                        "verified", false
                    ));
            }
        } catch (Exception e) {
            logger.error("商家電子郵件驗證過程發生異常 - 令牌: {}", token, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "驗證過程發生錯誤：" + e.getMessage()));
        }
    }
} 