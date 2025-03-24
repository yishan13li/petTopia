package petTopia.controller.user;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;

import petTopia.jwt.JwtUtil;
import petTopia.model.user.User;
import petTopia.service.user.VendorLoginService;

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

            User user = (User) loginResult.get("user");
            
            // 創建認證令牌
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 生成 JWT，設置較長的過期時間（例如 7 天）
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
    public ResponseEntity<?> getVendorProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }

            String email = authentication.getName();
            User user = vendorService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "用戶不存在"));
            }

            Map<String, Object> vendorInfo = vendorService.getVendorInfo(user.getId());
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

            User user = (User) loginResult.get("user");
            
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
    public ResponseEntity<?> getLoginStatus() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.ok(Map.of("isLoggedIn", false));
            }

            String email = authentication.getName();
            User user = vendorService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.ok(Map.of("isLoggedIn", false));
            }

            Map<String, Object> vendorInfo = vendorService.getVendorInfo(user.getId());
            
            return ResponseEntity.ok(Map.of(
                "isLoggedIn", true,
                "userId", user.getId(),
                "vendorName", vendorInfo.get("vendorName"),
                "email", email,
                "role", user.getUserRole().toString()
            ));
        } catch (Exception e) {
            logger.error("獲取登入狀態失敗", e);
            return ResponseEntity.ok(Map.of("isLoggedIn", false));
        }
    }

    /**
     * 檢查用戶是否有資格成為商家
     */
    @GetMapping("/convert/check")
    public ResponseEntity<?> checkVendorEligibility() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }

            String email = authentication.getName();
            User user = vendorService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "用戶不存在"));
            }

            // 檢查用戶是否已經是商家
            if (user.getUserRole().toString().equals("VENDOR")) {
                return ResponseEntity.ok(Map.of(
                    "eligible", false,
                    "hasExistingAccount", true,
                    "message", "您已經是商家身份"
                ));
            }

            // 檢查用戶是否有未完成的商家申請
            boolean hasPendingApplication = vendorService.hasPendingVendorApplication(user.getId());
            if (hasPendingApplication) {
                return ResponseEntity.ok(Map.of(
                    "eligible", false,
                    "hasExistingAccount", false,
                    "message", "您有未完成的商家申請，請等待審核"
                ));
            }

            // 檢查用戶是否有被拒絕的商家申請
            boolean hasRejectedApplication = vendorService.hasRejectedVendorApplication(user.getId());
            if (hasRejectedApplication) {
                return ResponseEntity.ok(Map.of(
                    "eligible", false,
                    "hasExistingAccount", false,
                    "message", "您的商家申請已被拒絕，請聯繫客服"
                ));
            }

            // 檢查用戶是否有資格成為商家
            boolean isEligible = vendorService.isEligibleForVendor(user.getId());
            if (!isEligible) {
                return ResponseEntity.ok(Map.of(
                    "eligible", false,
                    "hasExistingAccount", false,
                    "message", "您目前不符合成為商家的資格"
                ));
            }

            return ResponseEntity.ok(Map.of(
                "eligible", true,
                "hasExistingAccount", false,
                "message", "您可以申請成為商家"
            ));

        } catch (Exception e) {
            logger.error("檢查商家資格失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "系統發生錯誤，請稍後再試"));
        }
    }

    /**
     * 將普通用戶轉換為商家
     */
    @PostMapping("/convert")
    public ResponseEntity<?> convertToVendor(@RequestBody Map<String, Boolean> request) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "請先登入"));
            }

            String email = authentication.getName();
            User user = vendorService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "用戶不存在"));
            }

            // 檢查用戶是否已經是商家
            if (user.getUserRole().toString().equals("VENDOR")) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "您已經是商家身份"));
            }

            // 檢查用戶是否有資格成為商家
            boolean isEligible = vendorService.isEligibleForVendor(user.getId());
            if (!isEligible) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "您目前不符合成為商家的資格"));
            }

            // 執行轉換
            Map<String, Object> result = vendorService.convertToVendor(user.getId());
            
            // 生成新的 JWT token
            String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getId(),
                "VENDOR"
            );

            // 更新用戶角色
            user.setUserRole(User.UserRole.VENDOR);
            vendorService.updateUser(user);

            logger.info("用戶 {} 成功轉換為商家", user.getId());

            return ResponseEntity.ok(Map.of(
                "success", true,
                "token", token,
                "vendorId", result.get("vendorId"),
                "email", user.getEmail(),
                "role", "VENDOR",
                "message", "成功轉換為商家"
            ));

        } catch (Exception e) {
            logger.error("轉換商家失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "系統發生錯誤，請稍後再試"));
        }
    }
}
