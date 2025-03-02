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

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UsersRepository usersRepository;
    
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);
        
        // 获取 Google 账号信息
        String email = oauth2User.getAttribute("email");
        String name = oauth2User.getAttribute("name");
        
        try {
            // 先检查是否存在对应的 UsersBean
            UsersBean user = usersRepository.findByEmail(email);
            MemberBean member = null;
            
            if (user == null) {
                // 创建新的 UsersBean
                user = new UsersBean();
                user.setEmail(email);
                user.setPassword(""); // Google 登录不需要密码
                user.setUserRole(UsersBean.UserRole.MEMBER);
                user.setEmailVerified(true);
                user.setProvider("GOOGLE");
                user = usersRepository.save(user); // 保存并获取生成的 ID
                entityManager.flush(); // 确保 ID 已生成
                
                // 创建新的 MemberBean
                member = new MemberBean();
                member.setId(user.getId()); // 设置相同的 ID
                member.setUser(user);
                member.setName(name != null ? name : "");
                member.setPhone(""); // 需要用户后续补充
                member.setStatus(true);
                member.setGender(false); // 设置默认值
                member.setUpdatedDate(LocalDateTime.now());
                
                // 保存 MemberBean
                entityManager.persist(member);
                entityManager.flush();
            } else {
                // 如果用户已存在，获取对应的 MemberBean
                member = memberRepository.findByEmail(email);
            }
            
            // 创建包含额外信息的属性Map
            Map<String, Object> attributes = new HashMap<>(oauth2User.getAttributes());
            attributes.put("userId", user.getId());
            attributes.put("userRole", user.getUserRole());
            attributes.put("memberName", member != null ? member.getName() : name);
            attributes.put("provider", user.getProvider());
            
            // 返回包含额外信息的OAuth2User
            return new DefaultOAuth2User(
                oauth2User.getAuthorities(),
                attributes,
                "email" // nameAttributeKey
            );
            
        } catch (Exception e) {
            OAuth2Error oauth2Error = new OAuth2Error(
                "user_creation_error",
                "Error creating user: " + e.getMessage(),
                null
            );
            throw new OAuth2AuthenticationException(oauth2Error, e);
        }
    }
} 