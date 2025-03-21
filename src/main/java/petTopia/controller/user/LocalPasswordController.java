package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import petTopia.model.user.User;
import petTopia.service.user.EmailService;
import petTopia.service.user.MemberLoginService;
import petTopia.repository.user.UserRepository; 

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 本地密碼控制器，允許第三方登入用戶設置本地密碼。
 */
@RestController
@RequestMapping("/api/auth/local-password")
public class LocalPasswordController {
    
    private static final Logger logger = LoggerFactory.getLogger(LocalPasswordController.class);
    
    @Autowired
    private MemberLoginService memberLoginService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UserRepository usersRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // 存儲驗證碼，格式為：email -> {code, timestamp}
    private Map<String, Map<String, Object>> verificationCodes = new HashMap<>();
    
    /**
     * 檢查用戶是否可以設置本地密碼。
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkEmailStatus(@RequestParam String email) {
        User user = memberLoginService.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", "找不到會員帳號"));
        }
        
        if (user.getProvider() == User.Provider.LOCAL) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "此帳號已是本地帳號"));
        }
        
        return ResponseEntity.ok(Map.of("email", email, "provider", user.getProvider(), "canSetupLocalPassword", true));
    }
    
    /**
     * 發送驗證碼到用戶電子郵件。
     */
    @PostMapping("/send-verification")
    public ResponseEntity<?> sendVerification(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "電子郵件不能為空"));
        }
        
        try {
            User user = memberLoginService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "找不到會員帳號"));
            }
            
            if (user.getProvider() == User.Provider.LOCAL) {
                return ResponseEntity.badRequest().body(Map.of("error", "此帳號已是本地帳號"));
            }
            
            // 生成 6 位數驗證碼
            String code = String.format("%06d", new Random().nextInt(1000000));
            
            // 存儲驗證碼和時間戳
            verificationCodes.put(email, Map.of("code", code, "timestamp", System.currentTimeMillis()));
            
            // 發送驗證郵件
            String emailContent = "您的驗證碼是: <b>" + code + "</b> (5 分鐘內有效)";
            emailService.sendHtmlEmail(email, "設置本地密碼驗證碼", emailContent);
            
            logger.info("已發送驗證碼至 {}", email);
            return ResponseEntity.ok(Map.of("message", "驗證碼已發送", "email", email));
        } catch (Exception e) {
            logger.error("發送驗證碼失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "發送驗證碼失敗: " + e.getMessage()));
        }
    }
    
    /**
     * 驗證用戶輸入的驗證碼。
     */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");
        
        if (email == null || code == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "電子郵件和驗證碼不能為空"));
        }
        
        Map<String, Object> codeData = verificationCodes.get(email);
        if (codeData == null || !code.equals(codeData.get("code"))) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", "驗證碼錯誤或已過期"));
        }
        
        return ResponseEntity.ok(Map.of("message", "驗證成功", "email", email, "verified", true));
    }
    
    /**
     * 設置新的本地密碼。
     */
    @PostMapping("/set-password")
    public ResponseEntity<?> setPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String password = request.get("password");
        
        if (email == null || password == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "電子郵件和密碼不能為空"));
        }
        
        try {
            User user = memberLoginService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "找不到用戶"));
            }
            
            user.setPassword(passwordEncoder.encode(password));
            user.setLocalEnabled(true); // 啟用本地密碼登入
            usersRepository.save(user);
            
            verificationCodes.remove(email);
            logger.info("成功為用戶 {} 設置本地密碼", email);
            return ResponseEntity.ok(Map.of("message", "密碼設置成功", "email", email));
        } catch (Exception e) {
            logger.error("設置密碼失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "設置密碼失敗: " + e.getMessage()));
        }
    }
}
