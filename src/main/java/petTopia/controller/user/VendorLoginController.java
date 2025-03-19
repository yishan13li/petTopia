package petTopia.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

import petTopia.model.user.Users;
import petTopia.service.user.VendorLoginService;
import petTopia.util.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/vendor/auth")
public class VendorLoginController {
    private static final Logger logger = LoggerFactory.getLogger(VendorLoginController.class);

    @Autowired
    private VendorLoginService vendorService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        logger.info("開始處理商家登入請求 - 電子郵件: {}", email);

        // 基本驗證
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "請輸入電子郵件"));
        }
        if (password == null || password.trim().isEmpty()) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "請輸入密碼"));
        }

        try {
            logger.debug("開始驗證商家資訊");
            Map<String, Object> loginResult = vendorService.vendorLogin(email, password);

            if (!(Boolean) loginResult.get("success")) {
                logger.warn("商家登入失敗 - {} - 電子郵件: {}", loginResult.get("message"), email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", loginResult.get("message")));
            }

            Users user = (Users) loginResult.get("user");
            
            // 創建認證令牌
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 生成 JWT
            String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getUserRole().toString());

            logger.info("商家登入成功 - 使用者ID: {}", user.getId());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "token", token,
                "userId", user.getId(),
                "vendorName", loginResult.get("vendorName"),
                "email", email,
                "role", user.getUserRole().toString(),
                "message", "登入成功"
            ));

        } catch (Exception e) {
            logger.error("登入過程發生異常 - 電子郵件: {} - 錯誤訊息: {}", email, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "系統發生錯誤，請稍後再試"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok(Map.of("message", "商家登出成功"));
        } catch (Exception e) {
            logger.error("登出過程發生異常", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "登出失敗：" + e.getMessage()));
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getVendorProfile(@RequestHeader(value = "Authorization", required = false) String authHeader) {
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
            if (!"VENDOR".equals(role)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "無權限訪問此資源"));
            }

            Integer userId = jwtUtil.extractUserId(token);
            Map<String, Object> vendorInfo = vendorService.getVendorInfo(userId);
            
            return ResponseEntity.ok(vendorInfo);
        } catch (Exception e) {
            logger.error("獲取商家資料失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲取商家資料失敗：" + e.getMessage()));
        }
    }

    @PostMapping("/oauth2/login")
    public ResponseEntity<?> handleOAuth2Login(@AuthenticationPrincipal OAuth2User oauth2User) {
        try {
            logger.info("處理OAuth2登入 - 提供者: {}", String.valueOf(oauth2User.getAttribute("provider")));
            
            String email = oauth2User.getAttribute("email");
            Map<String, Object> loginResult = vendorService.vendorOAuth2Login(email);

            if (!(Boolean) loginResult.get("success")) {
                logger.warn("OAuth2商家登入失敗 - {} - 電子郵件: {}", loginResult.get("message"), email);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", loginResult.get("message")));
            }

            Users user = (Users) loginResult.get("user");
            
            // 創建認證令牌
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, oauth2User.getAttribute("provider") + "_" + email)
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 生成 JWT
            String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getUserRole().toString());

            logger.info("OAuth2商家登入成功 - 使用者ID: {}", user.getId());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "token", token,
                "userId", user.getId(),
                "vendorName", loginResult.get("vendorName"),
                "email", email,
                "role", user.getUserRole().toString(),
                "provider", oauth2User.getAttribute("provider"),
                "message", "OAuth2 登入成功"
            ));

        } catch (Exception e) {
            logger.error("OAuth2登入過程發生異常 - 錯誤訊息: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "系統發生錯誤，請稍後再試"));
        }
    }

    @GetMapping("/status")
    public ResponseEntity<?> getLoginStatus(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(Map.of("isLoggedIn", false));
        }

        String token = authHeader.substring(7);
        try {
            String email = jwtUtil.extractUsername(token);
            if (!jwtUtil.validateToken(token, email)) {
                return ResponseEntity.ok(Map.of("isLoggedIn", false));
            }

            String role = jwtUtil.extractUserRole(token);
            if (!"VENDOR".equals(role)) {
                return ResponseEntity.ok(Map.of("isLoggedIn", false));
            }

            Integer userId = jwtUtil.extractUserId(token);
            Map<String, Object> vendorInfo = vendorService.getVendorInfo(userId);
            
            return ResponseEntity.ok(Map.of(
                "isLoggedIn", true,
                "userId", userId,
                "vendorName", vendorInfo.get("vendorName"),
                "email", email,
                "role", role
            ));
        } catch (Exception e) {
            logger.error("獲取登入狀態失敗", e);
            return ResponseEntity.ok(Map.of("isLoggedIn", false));
        }
    }
}
