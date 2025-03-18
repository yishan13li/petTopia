package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import petTopia.model.user.Users;
import petTopia.service.user.MemberLoginService;
import petTopia.model.user.Member;
import petTopia.service.user.MemberService;
import petTopia.util.JwtUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class MemberLoginController {
    private static final Logger logger = LoggerFactory.getLogger(MemberLoginController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MemberLoginService memberLoginService;

    @Autowired
    private MemberService memberService;

    /**
     * 檢查名稱是否為郵箱格式
     */
    private boolean isEmailFormat(String name, String email) {
        if (name == null || email == null) return false;
        
        // 最基本的判斷：名稱與郵箱完全相同
        if (name.equalsIgnoreCase(email)) return true;
        
        // 更進階的判斷：名稱是否符合郵箱格式 (包含 @ 和 .)
        return name.matches("^[^@]+@[^@]+\\.[^@]+$");
    }
    
    /**
     * 獲取更友好的顯示名稱
     */
    private String getFriendlyDisplayName(String name, String email) {
        if (isEmailFormat(name, email)) {
            // 如果名稱是郵箱格式，使用郵箱的用戶名部分
            String emailUsername = email.split("@")[0];
            logger.info("名稱是郵箱格式，轉換為更友好的格式: {} -> {}", name, emailUsername);
            return emailUsername;
        }
        return name;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        if (email == null || password == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "電子郵件和密碼不能為空"));
        }

        try {
            Map<String, Object> loginResult = memberLoginService.memberLogin(email, password);
            
            // 檢查是否是第三方登入帳號
            if (loginResult.containsKey("isThirdPartyAccount") && (Boolean) loginResult.get("isThirdPartyAccount")) {
                Users.Provider provider = (Users.Provider) loginResult.get("provider");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                        "error", "此帳號是使用" + provider.toString() + "註冊的，請使用對應的登入方式，或設定本地密碼",
                        "isThirdPartyAccount", true,
                        "provider", provider.toString(),
                        "email", email
                    ));
            }
            
            if ((Boolean) loginResult.get("success")) {
                Users user = (Users) loginResult.get("user");
                
                // 創建認證令牌
                Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
                );
                
                // 設置安全上下文
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // 生成 JWT
                String token = jwtUtil.generateToken(user.getEmail(), user.getId(), user.getUserRole().toString());
                
                Member member = memberService.getMemberById(user.getId());
                if (member == null) {
                    // 如果會員資料不存在，創建新的會員資料
                    member = new Member();
                    member.setId(user.getId());
                    member.setUser(user);
                    
                    // 使用友好的顯示名稱
                    String friendlyName = getFriendlyDisplayName(user.getEmail().split("@")[0], user.getEmail());
                    member.setName(friendlyName);
                    member.setStatus(false);
                    member = memberService.createOrUpdateMember(member);
                }
                
                // 檢查名稱是否為郵箱格式
                String displayName = member.getName();
                if (isEmailFormat(displayName, email)) {
                    displayName = getFriendlyDisplayName(displayName, email);
                    
                    // 如果名稱已更改，更新資料庫
                    if (!displayName.equals(member.getName())) {
                        member.setName(displayName);
                        memberService.createOrUpdateMember(member);
                    }
                }
                
                Map<String, Object> response = new HashMap<>();
                response.put("token", token);
                response.put("userId", user.getId());
                response.put("email", user.getEmail());
                response.put("name", displayName);
                response.put("memberName", displayName); // 同時設置 memberName
                response.put("role", user.getUserRole().name());
                response.put("provider", user.getProvider().name());
                response.put("message", "登入成功");
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", loginResult.get("message")));
            }
        } catch (Exception e) {
            logger.error("登入失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "登入失敗: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // JWT 不需要伺服器端登出操作
        // 客戶端只需要移除本地儲存的 token
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "登出成功"));
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

            Integer userId = jwtUtil.extractUserId(token);
            Member member = memberService.getMemberById(userId);

            if (member == null) {
                return ResponseEntity.ok(Map.of("isLoggedIn", false));
            }
            
            // 獲取用戶資料
            Users user = memberLoginService.findByEmail(email);
            
            // 檢查名稱是否為郵箱格式，如果是則使用更友好的格式
            String displayName = member.getName();
            if (user != null && user.getProvider() != Users.Provider.LOCAL && isEmailFormat(displayName, email)) {
                displayName = getFriendlyDisplayName(displayName, email);
                
                // 如果名稱已更改，更新資料庫
                if (!displayName.equals(member.getName())) {
                    logger.info("更新會員資料中的名稱為更友好的格式: {} -> {}", member.getName(), displayName);
                    member.setName(displayName);
                    memberService.createOrUpdateMember(member);
                }
            }
            
            // 使用 HashMap 支持更多鍵值對
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("isLoggedIn", true);
            responseData.put("userId", userId);
            responseData.put("email", email);
            responseData.put("name", displayName);
            responseData.put("memberName", displayName); // 同時設置 memberName
            responseData.put("role", jwtUtil.extractUserRole(token));
            
            // 如果有用戶資料，添加提供者資訊
            if (user != null) {
                responseData.put("provider", user.getProvider().toString());
            }
            
            return ResponseEntity.ok(responseData);
        } catch (Exception e) {
            logger.error("獲取登入狀態失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲取登入狀態失敗: " + e.getMessage()));
        }
    }
}
