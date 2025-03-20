package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import petTopia.model.user.User;
import petTopia.service.user.RegistrationService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class RegistrationController {

    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);

    @Autowired
    private RegistrationService registrationService;

    /**
     * 檢查 email 是否已存在
     */
    @GetMapping("/check-email")
    public ResponseEntity<?> checkEmail(@RequestParam String email) {
        logger.info("檢查 email 是否已存在 - email: {}", email);
        
        if (email == null || email.trim().isEmpty()) {
            logger.warn("檢查 email 失敗 - email 為空");
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Email 不能為空"));
        }
        
        try {
            String trimmedEmail = email.toLowerCase().trim();
            User existingUser = registrationService.findByEmail(trimmedEmail);
            
            Map<String, Object> response = new HashMap<>();
            
            if (existingUser != null) {
                String message;
                if (existingUser.getProvider() == User.Provider.GOOGLE) {
                    message = "此 email 已使用 Google 帳號登入過，請點擊「使用 Google 登入」按鈕";
                } else if (existingUser.getUserRole() == User.UserRole.VENDOR) {
                    message = "此 email 已註冊為商家帳號，請使用其他 email 註冊會員";
                } else {
                    message = "此 email 已註冊為會員帳號，請直接登入";
                }
                
                logger.info("Email 已存在 - email: {}, 用戶類型: {}", 
                    trimmedEmail, existingUser.getUserRole());
                
                response.put("exists", true);
                response.put("message", message);
                response.put("userRole", existingUser.getUserRole());
                response.put("provider", existingUser.getProvider());
            } else {
                logger.info("Email 可用 - email: {}", trimmedEmail);
                response.put("exists", false);
                response.put("message", "此 email 可用於註冊");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("檢查 email 時發生錯誤", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "檢查 email 時發生錯誤：" + e.getMessage()));
        }
    }
} 