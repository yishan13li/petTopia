package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import petTopia.model.user.MemberBean;
import petTopia.model.user.UsersBean;
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
import petTopia.model.user.VendorBean;
import petTopia.repository.user.VendorRepository;

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
        
        try {
            UsersBean user = usersRepository.findByEmail(email);
            
            if (user != null) {
                // 檢查是否是商家帳號
                if (user.getUserRole() == UsersBean.UserRole.VENDOR) {
                    throw new OAuth2AuthenticationException(
                        new OAuth2Error("account_exists",
                        "商家帳號請使用一般登入方式，不支援Google登入", null)
                    );
                }
                
                // 檢查是否是本地註冊的會員帳號
                if (user.getProvider() == UsersBean.Provider.LOCAL) {
                    throw new OAuth2AuthenticationException(
                        new OAuth2Error("account_exists",
                        "此Email已使用本地註冊為會員帳號，請使用密碼登入", null)
                    );
                }
                
                // 更新會員資訊
                MemberBean member = memberRepository.findByEmail(email);
                if (member != null) {
                    member.setName(name != null ? name : member.getName());
                    member.setUpdatedDate(LocalDateTime.now());
                    memberRepository.save(member);
                }
            } else {
                // 建立新的會員帳號
                user = new UsersBean();
                user.setEmail(email);
                user.setPassword(""); // Google 登入不需要密碼
                user.setUserRole(UsersBean.UserRole.MEMBER);
                user.setEmailVerified(true);
                user.setProvider(UsersBean.Provider.GOOGLE);
                user = usersRepository.save(user);
                entityManager.flush();
                
                // 建立新的會員資料
                MemberBean member = new MemberBean();
                member.setId(user.getId());
                member.setUser(user);
                member.setName(name != null ? name : "");
                member.setPhone("");
                member.setStatus(true);
                member.setGender(false);
                member.setUpdatedDate(LocalDateTime.now());
                
                entityManager.persist(member);
                entityManager.flush();
            }
            
            // 建立包含額外資訊的屬性Map
            Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
            attributes.put("userId", user.getId());
            attributes.put("userRole", user.getUserRole());
            attributes.put("memberName", name);
            attributes.put("provider", user.getProvider());
            
            return new DefaultOAuth2User(
                oauth2User.getAuthorities(),
                attributes,
                "email"
            );
            
        } catch (OAuth2AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Google登入處理失敗", e);
            throw new OAuth2AuthenticationException(
                new OAuth2Error("processing_error",
                "處理Google登入時發生錯誤: " + e.getMessage(), null)
            );
        }
    }
} 