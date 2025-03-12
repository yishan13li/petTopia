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
import petTopia.model.user.Users;
import petTopia.repository.user.MemberRepository;
import petTopia.repository.user.UsersRepository;
import java.time.LocalDateTime;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petTopia.model.user.Vendor;
import petTopia.repository.user.VendorRepository;
import java.util.List;
import java.util.Optional;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2UserService.class);

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UsersRepository usersRepository;
    
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
            List<Users> localUsers = usersRepository.findByEmailAndProviderAndUserRole(
                email, Users.Provider.LOCAL, Users.UserRole.MEMBER);
            
            if (!localUsers.isEmpty()) {
                // 如果存在本地會員帳號，使用第一個找到的帳號進行綁定
                Users localUser = localUsers.get(0);
                
                // 如果有多個本地會員帳號，記錄警告
                if (localUsers.size() > 1) {
                    logger.warn("發現多個本地會員帳號使用相同的郵箱: {}", email);
                }
                
                // 檢查是否有對應的商家帳號
                Optional<Users> vendorAccount = usersRepository.findVendorByEmail(email);
                
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
                
                return new DefaultOAuth2User(
                    oauth2User.getAuthorities(),
                    attributes,
                    "email"
                );
            }
            
            // 查找是否有相同 email 的第三方登入會員帳號
            Users existingUser = usersRepository.findByEmailAndUserRole(email, Users.UserRole.MEMBER);
            
            if (existingUser != null) {
                // 更新會員資訊
                Member member = memberRepository.findByUserId(existingUser.getId()).orElse(null);
                if (member != null) {
                    member.setName(name != null ? name : member.getName());
                    member.setUpdatedDate(LocalDateTime.now());
                    memberRepository.save(member);
                }
                
                // 如果提供者不同，更新提供者資訊
                if (existingUser.getProvider() != Users.Provider.valueOf(provider)) {
                    existingUser.setProvider(Users.Provider.valueOf(provider));
                    usersRepository.save(existingUser);
                }
                
                // 檢查是否有對應的商家帳號
                Optional<Users> vendorAccount = usersRepository.findVendorByEmail(email);
                
                // 建立包含額外資訊的屬性Map
                Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
                attributes.put("userId", existingUser.getId());
                attributes.put("userRole", existingUser.getUserRole());
                attributes.put("memberName", name);
                attributes.put("provider", existingUser.getProvider());
                attributes.put("hasVendorAccount", vendorAccount.isPresent());
                if (vendorAccount.isPresent()) {
                    attributes.put("vendorId", vendorAccount.get().getId());
                }
                
                return new DefaultOAuth2User(
                    oauth2User.getAuthorities(),
                    attributes,
                    "email"
                );
            } else {
                // 建立新的會員帳號
                existingUser = new Users();
                existingUser.setEmail(email);
                existingUser.setPassword(""); // 第三方登入不需要密碼
                existingUser.setUserRole(Users.UserRole.MEMBER);
                existingUser.setEmailVerified(true);
                existingUser.setProvider(Users.Provider.valueOf(provider));
                existingUser = usersRepository.save(existingUser);
                entityManager.flush();
                
                // 建立新的會員資料
                Member member = new Member();
                member.setId(existingUser.getId());
                member.setUser(existingUser);
                member.setName(name != null ? name : "");
                member.setPhone("");
                member.setStatus(true);
                member.setGender(false);
                member.setUpdatedDate(LocalDateTime.now());
                
                entityManager.persist(member);
                entityManager.flush();
                
                // 建立包含額外資訊的屬性Map
                Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
                attributes.put("userId", existingUser.getId());
                attributes.put("userRole", existingUser.getUserRole());
                attributes.put("memberName", name);
                attributes.put("provider", existingUser.getProvider());
                attributes.put("hasVendorAccount", false);
                
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