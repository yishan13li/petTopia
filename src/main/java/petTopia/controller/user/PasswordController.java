package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import petTopia.model.user.User;
import petTopia.service.user.MemberLoginService;
import petTopia.repository.user.UserRepository;

import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class PasswordController {
    
    private static final Logger logger = LoggerFactory.getLogger(PasswordController.class);
    
    @Autowired
    private MemberLoginService memberLoginService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");
        
        if (email == null || newPassword == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "電子郵件和密碼不能為空"));
        }
        
        try {
            // 獲取當前用戶
            User user = memberLoginService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "找不到用戶"));
            }
            
            // 更新密碼
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
            
            logger.info("用戶 {} 密碼更改成功", email);
            return ResponseEntity.ok(Map.of(
                "message", "密碼更改成功",
                "email", email
            ));
        } catch (Exception e) {
            logger.error("更改密碼失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "更改密碼失敗：" + e.getMessage()));
        }
    }
} 