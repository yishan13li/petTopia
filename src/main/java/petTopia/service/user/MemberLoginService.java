package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import petTopia.model.user.User;
import petTopia.model.user.Member;
import petTopia.repository.user.UserRepository;
import petTopia.repository.user.MemberRepository;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@Transactional
public class MemberLoginService extends BaseUserService {
    private static final Logger logger = LoggerFactory.getLogger(MemberLoginService.class);
    
    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // 添加默認構造函數
    public MemberLoginService() {
        logger.info("創建 MemberLoginService 實例");
    }

    public Map<String, Object> memberLogin(String email, String password) {
        Map<String, Object> result = new HashMap<>();
        logger.info("開始會員登入流程，email: {}", email);

        try {
            User user = usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER);

            if (user == null) {
                logger.warn("登入失敗：會員帳號不存在，email: {}", email);
                result.put("success", false);
                result.put("message", "會員帳號不存在");
                return result;
            }

            // 檢查是否是第三方登入帳號且未啟用本地密碼
            if (user.getProvider() != User.Provider.LOCAL && !user.isLocalEnabled()) {
                logger.warn("登入失敗：第三方登入帳號嘗試使用密碼登入，userId: {}, provider: {}", user.getId(), user.getProvider());
                result.put("success", false);
                result.put("message", "此帳號是使用" + user.getProvider().toString() + "註冊的，請使用對應的登入方式，或設定本地密碼");
                result.put("isThirdPartyAccount", true);
                result.put("provider", user.getProvider());
                result.put("email", user.getEmail());
                return result;
            }

            if (!passwordEncoder.matches(password, user.getPassword())) {
                logger.warn("登入失敗：密碼錯誤，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "密碼錯誤");
                return result;
            }

            // 檢查郵箱驗證狀態
            if (!user.isEmailVerified()) {
                logger.warn("登入失敗：郵箱未驗證，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "您的郵箱尚未驗證，請查收驗證郵件並完成驗證");
                result.put("needVerification", true);
                return result;
            }

            // 獲取會員信息
            Member member = memberRepository.findByUserId(user.getId()).orElse(null);
            
            result.put("success", true);
            result.put("message", "登入成功，歡迎回來！");
            result.put("user", user);
            result.put("userId", user.getId());
            result.put("memberName", member != null ? member.getName() : user.getEmail().split("@")[0]);
            result.put("userRole", user.getUserRole());
            result.put("email", user.getEmail());
            result.put("loggedInUser", user);
            logger.info("會員登入成功，userId: {}, email: {}", user.getId(), user.getEmail());

        } catch (Exception e) {
            logger.error("會員登入過程發生錯誤", e);
            result.put("success", false);
            result.put("message", "登入失敗：" + e.getMessage());
        }

        return result;
    }

    public class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
    }

    public User findByEmail(String email) {
        return usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER);
    }

    public User findById(Integer id) {
        return usersRepository.findById(id).orElse(null);
    }

    public User updateUser(User user) {
        return usersRepository.save(user);
    }

    public Map<String, Object> getMemberInfo(User user) {
        Map<String, Object> memberInfo = new HashMap<>();
        try {
            Member member = memberRepository.findByUserId(user.getId()).orElse(null);
            
            memberInfo.put("userId", user.getId());
            memberInfo.put("email", user.getEmail());
            memberInfo.put("userRole", user.getUserRole());
            memberInfo.put("name", member != null ? member.getName() : user.getEmail().split("@")[0]);
            memberInfo.put("provider", user.getProvider());
            memberInfo.put("avatar", null); // 如果需要頭像，可以從 member.getProfilePhoto() 轉換
            memberInfo.put("memberName", member != null ? member.getName() : user.getEmail().split("@")[0]);
            
            return memberInfo;
        } catch (Exception e) {
            logger.error("獲取會員信息時發生錯誤", e);
            throw new RuntimeException("獲取會員信息失敗：" + e.getMessage());
        }
    }
}