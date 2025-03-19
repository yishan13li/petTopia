package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import petTopia.service.user.UsersService;
import petTopia.jwt.JwtUtil;
import petTopia.model.user.Users;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/auth/oauth2")
public class OAuth2BindController {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2BindController.class);

    @Autowired
    private UsersService usersService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    // 獲取綁定資訊
    @GetMapping("/bind-info")
    public ResponseEntity<?> getBindInfo() {
        // This method is no longer used in the new implementation
        return ResponseEntity.badRequest()
            .body(Map.of("error", "This method is no longer used in the new implementation"));
    }

    // 執行 OAuth2 帳號綁定
    @PostMapping("/bind")
    public ResponseEntity<?> bindAccount(@RequestBody Map<String, Object> bindData) {
        try {
            Integer localUserId = (Integer) bindData.get("localUserId");
            String provider = (String) bindData.get("provider");
            String email = (String) bindData.get("email");
            
            if (localUserId == null || provider == null || email == null) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "缺少綁定所需的資訊"));
            }

            logger.info("執行 OAuth2 帳號綁定 - 用戶ID: {}, 提供者: {}", localUserId, provider);
            
            // 檢查用戶是否存在
            Users user = usersService.findById(localUserId);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "用戶不存在"));
            }

            // 執行綁定
            try {
                usersService.bindOAuth2Account(localUserId, Users.Provider.valueOf(provider));
            } catch (Exception e) {
                logger.error("OAuth2 帳號綁定過程發生錯誤", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "帳號綁定失敗：" + e.getMessage()));
            }

            // 重新獲取更新後的用戶資訊
            user = usersService.findById(localUserId);
            
            // 生成新的 JWT
            String token = jwtUtil.generateToken(
                user.getEmail(), 
                user.getId(), 
                user.getUserRole().toString()
            );

            logger.info("OAuth2 帳號綁定成功 - 用戶ID: {}", localUserId);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "帳號綁定成功",
                "token", token,
                "userId", user.getId(),
                "email", user.getEmail(),
                "role", user.getUserRole().toString(),
                "provider", user.getProvider().toString()
            ));
            
        } catch (Exception e) {
            logger.error("OAuth2 帳號綁定失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "帳號綁定失敗：" + e.getMessage()));
        }
    }
}
