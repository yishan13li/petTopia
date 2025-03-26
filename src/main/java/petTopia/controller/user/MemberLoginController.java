package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import petTopia.model.user.User;
import petTopia.service.user.MemberLoginService;
import petTopia.jwt.JwtUtil;
import petTopia.model.user.Member;
import petTopia.service.user.MemberService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * JWT 認證流程說明
 * 
 * 1. 登入流程
 *    - 用戶輸入帳號密碼
 *    - 系統驗證帳號密碼
 *    - 驗證成功後生成 JWT 令牌
 * 
 * 2. JWT 令牌組成
 *    - Header（標頭）：包含令牌類型和加密算法
 *    - Payload（負載）：包含用戶資訊（ID、角色等）
 *    - Signature（簽名）：確保令牌未被篡改
 * 
 * 3. 安全機制
 *    - 使用 Bearer Token 認證
 *    - 令牌有效期限制
 *    - 簽名驗證機制
 * 
 * 4. 使用方式
 *    - 前端：在請求標頭加入 Authorization: Bearer {token}
 *    - 後端：驗證令牌並提取用戶資訊
 * 
 */

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

    /**
     * 登入處理流程
     * 
     * 1. 驗證流程
     *    - 檢查帳號密碼
     *    - 驗證用戶狀態
     *    - 檢查登入權限
     * 
     * 2. JWT 生成流程
     *    - 創建認證物件
     *    - 設置安全上下文
     *    - 生成 JWT 令牌
     * 
     * 3. 回傳資料
     *    - 成功：返回 JWT 令牌和用戶資訊
     *    - 失敗：返回錯誤訊息
     */
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
                User.Provider provider = (User.Provider) loginResult.get("provider");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                        "error", "此帳號是使用" + provider.toString() + "註冊的，請使用對應的登入方式，或設定本地密碼",
                        "isThirdPartyAccount", true,
                        "provider", provider.toString(),
                        "email", email
                    ));
            }
            
            if ((Boolean) loginResult.get("success")) {
                User user = (User) loginResult.get("user");
                
                /**
                 * 創建認證令牌
                 * 1. 使用 AuthenticationManager 進行認證
                 * 2. 創建 UsernamePasswordAuthenticationToken
                 * 3. 包含用戶名和密碼資訊
                 */
                Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
                );
                
                /**
                 * 設置安全上下文
                 * 1. 將認證資訊存入 SecurityContext
                 * 2. 供後續請求使用
                 * 3. 確保請求安全性
                 */
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                /**
                 * 生成 JWT 令牌
                 * 1. 包含用戶郵箱（作為唯一識別）
                 * 2. 包含用戶 ID
                 * 3. 包含用戶角色
                 * 4. 設置過期時間
                 */
                String token = jwtUtil.generateToken(
                    user.getEmail(),    // 用戶郵箱
                    user.getId(),       // 用戶 ID
                    user.getUserRole().toString()  // 用戶角色
                );
                
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
                response.put("memberId", member.getId());
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
            /**
             * 錯誤處理
             * 1. 記錄錯誤日誌
             * 2. 回傳適當的錯誤訊息
             * 3. 確保安全性
             */
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
            User user = memberLoginService.findByEmail(email);
            
            // 檢查名稱是否為郵箱格式，如果是則使用更友好的格式
            String displayName = member.getName();
            if (user != null && user.getProvider() != User.Provider.LOCAL && isEmailFormat(displayName, email)) {
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
