package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import petTopia.model.user.Member;
import petTopia.model.user.User;
import petTopia.model.vendor.Vendor;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.user.UserRepository;
import petTopia.repository.vendor.VendorRepository;

import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2UserService.class);

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository usersRepository;
    
    @Autowired
    private VendorRepository vendorRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        String provider = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        
        try {
            // 只檢查會員角色的本地帳號
            List<User> localUsers = usersRepository.findByEmailAndProviderAndUserRole(
                email, User.Provider.LOCAL, User.UserRole.MEMBER);
            
            if (!localUsers.isEmpty()) {
                // 如果存在本地會員帳號，使用第一個找到的帳號進行綁定
                User localUser = localUsers.get(0);
                
                // 如果有多個本地會員帳號，記錄警告
                if (localUsers.size() > 1) {
                    logger.warn("發現多個本地會員帳號使用相同的郵箱: {}", email);
                }
                
                // 檢查是否有對應的商家帳號
                Optional<User> vendorAccount = usersRepository.findVendorByEmail(email);
                
                Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
                attributes.put("localAccountExists", true);
                attributes.put("localUserId", localUser.getId());
                attributes.put("localUserRole", localUser.getUserRole());
                attributes.put("email", email);
                attributes.put("provider", provider);
                attributes.put("hasVendorAccount", vendorAccount.isPresent());
                if (vendorAccount.isPresent()) {
                    attributes.put("vendorId", vendorAccount.get().getId());
                }
                
                // 使用預設頭像
                attributes.put("avatar", "/user_static/images/default-avatar.png");
                
                // 添加所有原始属性
                attributes.putAll(oauth2User.getAttributes());
                
                logger.info("最終的用戶屬性: {}", attributes);
                
                return new DefaultOAuth2User(
                    oauth2User.getAuthorities(),
                    attributes,
                    "email"
                );
            }
            
            // 查找是否有相同 email 的第三方登入會員帳號
            List<User> existingUsers = usersRepository.findByEmailAndProviderAndUserRole(
                email, User.Provider.valueOf(provider), User.UserRole.MEMBER);
            
            User existingUser = existingUsers.isEmpty() ? null : existingUsers.get(0);
            
            if (existingUser != null) {
                // 獲取會員資訊
                Member member = memberRepository.findByUserId(existingUser.getId()).orElse(null);
                logger.info("找到現有用戶 - ID: {}, Email: {}, Provider: {}", 
                    existingUser.getId(), email, provider);
                logger.info("對應的會員信息 - Member: {}", member);
                
                // 檢查是否有對應的商家帳號
                Optional<User> vendorAccount = usersRepository.findVendorByEmail(email);
                
                // 建立包含額外資訊的屬性Map
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("userId", existingUser.getId());
                attributes.put("userRole", existingUser.getUserRole());
                attributes.put("email", email);
                attributes.put("provider", existingUser.getProvider());
                attributes.put("hasVendorAccount", vendorAccount.isPresent());
                
                // 设置用户名称
                if (member != null && member.getName() != null && !member.getName().trim().isEmpty()) {
                    logger.info("使用會員名稱: {}", member.getName());
                    attributes.put("name", member.getName());
                    attributes.put("memberName", member.getName());
                } else if (name != null && !name.trim().isEmpty()) {
                    logger.info("使用第三方提供的名稱: {}", name);
                    attributes.put("name", name);
                    attributes.put("memberName", name);
                } else {
                    String defaultName = email.split("@")[0];
                    logger.info("使用郵箱前綴作為名稱: {}", defaultName);
                    attributes.put("name", defaultName);
                    attributes.put("memberName", defaultName);
                }
                
                if (vendorAccount.isPresent()) {
                    attributes.put("vendorId", vendorAccount.get().getId());
                }
                
                // 使用預設頭像
                attributes.put("avatar", "/user_static/images/default-avatar.png");
                
                // 添加所有原始属性
                attributes.putAll(oauth2User.getAttributes());
                
                logger.info("最終的用戶屬性: {}", attributes);
                
                return new DefaultOAuth2User(
                    oauth2User.getAuthorities(),
                    attributes,
                    "email"
                );
            } else {
                // 建立新的會員帳號
                existingUser = new User();
                existingUser.setEmail(email);
                existingUser.setPassword(""); // 第三方登入不需要密碼
                existingUser.setUserRole(User.UserRole.MEMBER);
                existingUser.setEmailVerified(true);
                existingUser.setProvider(User.Provider.valueOf(provider));
                existingUser = usersRepository.saveAndFlush(existingUser);
                
                // 建立新的會員資料
                Member member = new Member();
                member.setUser(existingUser);
                
                // 设置会员名称
                String memberName;
                if (name != null && !name.trim().isEmpty()) {
                    memberName = name;
                } else {
                    memberName = email.split("@")[0];
                }
                member.setName(memberName);
                member.setPhone("");
                member.setStatus(true);
                member.setGender(false);
                member.setUpdatedDate(LocalDateTime.now());
                
                // 設置預設頭像
                try {
                    String defaultAvatarPath = "/static/user_static/images/default-avatar.png";
                    byte[] defaultAvatarBytes = getClass().getResourceAsStream(defaultAvatarPath).readAllBytes();
                    member.setProfilePhoto(defaultAvatarBytes);
                } catch (Exception e) {
                    logger.error("設置預設頭像失敗", e);
                }
                
                // 使用 memberRepository 保存会员资料
                member = memberRepository.saveAndFlush(member);
                
                // 建立包含額外資訊的屬性Map
                Map<String, Object> attributes = new HashMap<>();
                attributes.put("userId", existingUser.getId());
                attributes.put("userRole", existingUser.getUserRole());
                attributes.put("email", email);
                attributes.put("name", memberName);
                attributes.put("memberName", memberName);
                attributes.put("provider", existingUser.getProvider());
                attributes.put("hasVendorAccount", false);
                
                // 使用預設頭像
                attributes.put("avatar", "/user_static/images/default-avatar.png");
                
                // 添加所有原始属性
                attributes.putAll(oauth2User.getAttributes());
                
                return new DefaultOAuth2User(
                    oauth2User.getAuthorities(),
                    attributes,
                    "email"
                );
            }
        } catch (OAuth2AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("第三方登入處理失敗", e);
            throw new OAuth2AuthenticationException(
                new OAuth2Error("processing_error",
                "處理第三方登入時發生錯誤: " + e.getMessage(), null)
            );
        }
    }
} 