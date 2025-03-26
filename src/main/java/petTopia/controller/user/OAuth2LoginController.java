package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;


import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import petTopia.model.user.User;
import petTopia.jwt.JwtUtil;
import petTopia.model.user.Member;
import petTopia.service.user.MemberLoginService;
import petTopia.service.user.MemberService;

import petTopia.repository.user.UserRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/oauth2")
public class OAuth2LoginController {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginController.class);



    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MemberLoginService memberLoginService;

    @Autowired
    private MemberService memberService;



    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository usersRepository;


    
    // 添加默認的空構造函數
    public OAuth2LoginController() {
        logger.info("創建 OAuth2LoginController 實例");
    }

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
    public ResponseEntity<?> oauth2Login(@RequestBody Map<String, String> data) {
        String email = data.get("email");
        String name = data.get("name");
        String provider = data.get("provider");

        if (email == null || provider == null) {
            logger.error("OAuth2登入失敗 - 缺少必要資訊: email={}, provider={}", email, provider);
            return ResponseEntity.badRequest()
                .body(Map.of("error", "缺少必要的資訊"));
        }

        try {
            // 確保email不為空且規範化
            email = email.toLowerCase().trim();
            
            // 如果name為null，使用email的用戶名部分
            if (name == null || name.trim().isEmpty()) {
                name = email.split("@")[0];
                logger.info("使用郵箱用戶名作為預設名稱: {}", name);
            }

            logger.info("處理OAuth2登入 - 電子郵件: {}, 名稱: {}, 提供者: {}", email, name, provider);
            
            // 驗證提供者是否支持
            User.Provider providerEnum;
            try {
                providerEnum = User.Provider.valueOf(provider.toUpperCase());
            } catch (IllegalArgumentException e) {
                logger.warn("不支持的OAuth2提供者: {}", provider);
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "不支持的登入方式: " + provider));
            }
            
            // 查找已存在用戶
            User existingUser = memberLoginService.findByEmail(email);

            if (existingUser != null) {
                // 檢查用戶角色
                if (existingUser.getUserRole() != User.UserRole.MEMBER) {
                    logger.warn("OAuth2登入失敗 - 非會員帳號 - 電子郵件: {}, 角色: {}", 
                        email, existingUser.getUserRole());
                    return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "此帳號不是會員帳號"));
                }
                
                // 檢查提供者是否匹配
                if (existingUser.getProvider() != providerEnum && existingUser.getProvider() != User.Provider.LOCAL) {
                    logger.warn("OAuth2登入失敗 - 提供者不匹配 - 電子郵件: {}, 期望提供者: {}, 實際提供者: {}", 
                        email, provider, existingUser.getProvider());
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                            "error", "此帳號使用了不同的登入方式",
                            "expectedProvider", existingUser.getProvider().toString()
                        ));
                }
                
                // 檢查會員資料是否存在，不存在則創建
                Member member = memberService.getMemberById(existingUser.getId());
                if (member == null) {
                    // 如果會員資料不存在，創建新的會員資料
                    member = new Member();
                    member.setId(existingUser.getId());
                    member.setUser(existingUser);
                    // 使用不是郵箱格式的友好名稱
                    member.setName(getFriendlyDisplayName(name, email));
                    member.setStatus(true); // 設為已驗證
                    
                    try {
                        member = memberService.createOrUpdateMember(member);
                        logger.info("已為現有用戶創建會員資料, userId: {}, name: {}", existingUser.getId(), member.getName());
                    } catch (Exception e) {
                        logger.error("創建會員資料失敗", e);
                        // 仍然繼續，因為用戶驗證已通過
                    }
                }
                
                // 獲取最終要顯示的名稱
                String displayName = member.getName();
                
                // 只有當資料庫中沒有名稱，且第三方提供了有效名稱時才更新
                if ((displayName == null || displayName.trim().isEmpty()) && name != null && !name.trim().isEmpty()) {
                    displayName = name;  // 使用 OAuth2 提供的原始名稱
                    // 更新會員資料
                    member.setName(displayName);
                    member = memberService.createOrUpdateMember(member);
                    logger.info("使用 OAuth2 提供的名稱更新會員資料: {}", displayName);
                } else if (displayName == null || displayName.trim().isEmpty()) {
                    // 如果資料庫中沒有名稱，且第三方也沒提供，使用郵箱用戶名
                    displayName = email.split("@")[0];
                    member.setName(displayName);
                    member = memberService.createOrUpdateMember(member);
                    logger.info("使用郵箱用戶名作為名稱: {}", displayName);
                }
                
                logger.info("OAuth2登入成功 - 用戶ID: {}, 顯示名稱: {}", existingUser.getId(), displayName);
                
                // 構建返回數據
                Map<String, Object> responseData = new HashMap<>();
                responseData.put("success", true);
                responseData.put("token", jwtUtil.generateToken(
                    existingUser.getEmail(), 
                    existingUser.getId(), 
                    existingUser.getUserRole().toString()
                ));
                responseData.put("userId", existingUser.getId());
                responseData.put("email", email);
                responseData.put("name", displayName);
                responseData.put("memberName", displayName);
                responseData.put("role", existingUser.getUserRole().toString());
                responseData.put("provider", providerEnum.toString());
                responseData.put("picture", data.get("picture"));
                responseData.put("avatar", data.get("picture"));
                responseData.put("hasVendorAccount", data.get("hasVendorAccount"));
                responseData.put("message", "OAuth2 登入成功");
                
                logger.info("返回的用戶資訊: {}", responseData);
                
                return ResponseEntity.ok(responseData);
            }
            
            // 創建新會員帳號
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUserRole(User.UserRole.MEMBER);
            
            // 生成安全的隨機密碼
            String randomPassword = UUID.randomUUID().toString();
            newUser.setPassword(passwordEncoder.encode(randomPassword));
            newUser.setProvider(providerEnum);
            newUser.setEmailVerified(true);  // OAuth2 登入的郵箱已驗證
            
            logger.info("創建新用戶 - 電子郵件: {}, 提供者: {}", email, provider);
            
            // 手動保存用戶而不是使用註冊服務
            try {
                newUser = usersRepository.save(newUser);
                usersRepository.flush(); // 強制立即寫入資料庫
                logger.info("新用戶創建成功 - 用戶ID: {}", newUser.getId());
            } catch (Exception e) {
                logger.error("創建新用戶失敗", e);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "註冊失敗：無法創建用戶 - " + e.getMessage()));
            }
            
            // 獲取友好的顯示名稱
            String friendlyName = getFriendlyDisplayName(name, email);
            
            // 直接創建會員資料
            Member newMember = new Member();
            newMember.setId(newUser.getId());
            newMember.setUser(newUser);
            newMember.setName(friendlyName); // 使用友好名稱
            newMember.setStatus(true); // 第三方登入的用戶默認已驗證
            newMember.setUpdatedDate(LocalDateTime.now());
            
            try {
                newMember = memberService.createOrUpdateMember(newMember);
                logger.info("新會員資料創建成功 - 會員ID: {}, 名稱: {}", newMember.getId(), newMember.getName());
            } catch (Exception e) {
                logger.error("創建會員資料失敗", e);
                // 繼續處理，因為用戶已創建
            }
            
            // 生成JWT
            String token = jwtUtil.generateToken(
                newUser.getEmail(), 
                newUser.getId(), 
                newUser.getUserRole().toString()
            );
            
            logger.info("OAuth2註冊並登入成功 - 用戶ID: {}, 名稱: {}", newUser.getId(), friendlyName);
            
            // 構建返回數據
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("success", true);
            responseData.put("token", token);
            responseData.put("userId", newUser.getId());
            responseData.put("email", email);
            responseData.put("name", friendlyName);
            responseData.put("memberName", friendlyName);
            responseData.put("role", newUser.getUserRole().toString());
            responseData.put("provider", providerEnum.toString());
            responseData.put("picture", data.get("picture"));
            responseData.put("avatar", data.get("picture"));
            responseData.put("hasVendorAccount", false);
            responseData.put("message", "OAuth2 註冊並登入成功");

            logger.info("返回的新用戶資訊: {}", responseData);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(responseData);
            
        } catch (Exception e) {
            logger.error("OAuth2 登入過程發生異常 - 電子郵件: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "系統發生錯誤，請稍後再試"));
        }
    }
} 