package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import petTopia.model.user.User;       
import petTopia.service.user.EmailService;
import petTopia.service.user.RegistrationService;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@RestController
@RequestMapping("/api/auth")
public class MemberRegisterController {
    
    private static final Logger logger = LoggerFactory.getLogger(MemberRegisterController.class);

    @Autowired
    private RegistrationService registrationService;

    @Autowired
    private EmailService emailService;



    private Map<String, Map<String, Object>> verificationCodes = new HashMap<>();

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        String confirmPassword = request.get("confirmPassword");
        
        logger.info("處理會員註冊請求 - 電子郵件: {}", email);
        
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
            // 檢查是否已存在相同email的會員帳號
            User existingUser = registrationService.findByEmail(email);
            if (existingUser != null) {
                logger.warn("註冊失敗 - 電子郵件已存在: {}", email);
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "此 email 已註冊為會員"));
            }

            // 創建用戶基本信息
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setUserRole(User.UserRole.MEMBER);
            newUser.setProvider(User.Provider.LOCAL);

            // 使用註冊服務處理註冊
            Map<String, Object> result = registrationService.register(newUser);

            if ((Boolean) result.get("success")) {
                logger.info("會員註冊成功 - 電子郵件: {}", email);
                return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of(
                        "message", "註冊成功，請查收驗證郵件後登入",
                        "email", email
                    ));
            } else {
                logger.warn("註冊失敗 - {}", result.get("message"));
                return ResponseEntity.badRequest()
                    .body(Map.of("error", result.get("message")));
            }

        } catch (Exception e) {
            logger.error("註冊過程發生異常 - 電子郵件: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "註冊失敗：" + e.getMessage()));
        }
    }

    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "電子郵件不能為空"));
        }
        
        logger.info("發送驗證碼 - 電子郵件: {}", email);

        try {
            // 生成6位數驗證碼
            String code = String.format("%06d", new Random().nextInt(1000000));

            // 存儲驗證碼和時間戳
            Map<String, Object> codeData = new HashMap<>();
            codeData.put("code", code);
            codeData.put("timestamp", System.currentTimeMillis());
            verificationCodes.put(email, codeData);

            // 發送驗證碼到郵箱
            emailService.sendVerificationCode(email, code);

            logger.info("驗證碼發送成功 - 電子郵件: {}", email);
            return ResponseEntity.ok(Map.of(
                "message", "驗證碼已發送",
                "email", email
            ));
        } catch (Exception e) {
            logger.error("驗證碼發送失敗 - 電子郵件: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "驗證碼發送失敗：" + e.getMessage()));
        }
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        
        if (email == null || code == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "電子郵件和驗證碼不能為空"));
        }
        
        logger.info("驗證驗證碼 - 電子郵件: {}", email);

        // 第一步：檢查內存中是否有驗證碼
        Map<String, Object> codeData = verificationCodes.get(email);
        if (codeData != null) {
            String storedCode = (String) codeData.get("code");
            long timestamp = (long) codeData.get("timestamp");

            // 檢查驗證碼是否過期（5分鐘）
            if (System.currentTimeMillis() - timestamp > 5 * 60 * 1000) {
                verificationCodes.remove(email);
                logger.warn("驗證失敗 - 內存中驗證碼已過期 - 電子郵件: {}", email);
                // 不立即返回錯誤，繼續檢查數據庫中的驗證碼
            } else if (code.equals(storedCode)) {
                // 如果內存中的驗證碼匹配，則嘗試同時更新數據庫中的驗證狀態
                try {
                    // 嘗試在數據庫中查找用戶並更新驗證狀態
                    User user = registrationService.findByEmail(email);
                    if (user != null) {
                        // 更新數據庫中的驗證狀態
                        user.setEmailVerified(true);
                        registrationService.updateUser(user);
                        logger.info("驗證成功(內存驗證碼) - 已更新數據庫狀態 - 電子郵件: {}", email);
                    }
                } catch (Exception e) {
                    logger.warn("內存驗證成功但更新數據庫失敗 - 電子郵件: {}", email, e);
                    // 即使數據庫更新失敗，仍然可以繼續，因為內存驗證已成功
                }
                
                // 清除內存中的驗證碼
                verificationCodes.remove(email);
                
                logger.info("驗證成功(內存驗證碼) - 電子郵件: {}", email);
                return ResponseEntity.ok(Map.of(
                    "message", "驗證成功",
                    "email", email,
                    "verified", true
                ));
            }
        }

        // 第二步：如果內存中沒有驗證碼或驗證失敗，則檢查數據庫
        try {
            logger.info("嘗試從數據庫驗證 - 電子郵件: {}, 驗證碼: {}", email, code);
            boolean verified = registrationService.verifyEmail(code);
            
            if (verified) {
                logger.info("驗證成功(數據庫驗證碼) - 電子郵件: {}", email);
                return ResponseEntity.ok(Map.of(
                    "message", "驗證成功",
                    "email", email,
                    "verified", true
                ));
            } else {
                logger.warn("驗證失敗(數據庫驗證碼) - 電子郵件: {}", email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "驗證碼錯誤或已過期"));
            }
        } catch (Exception e) {
            logger.error("驗證過程發生數據庫錯誤 - 電子郵件: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "驗證過程發生錯誤: " + e.getMessage()));
        }
    }
}