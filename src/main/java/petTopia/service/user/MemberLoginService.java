package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import petTopia.model.user.UsersBean;
import petTopia.model.user.MemberBean;
import petTopia.repository.user.UsersRepository;
import petTopia.repository.user.MemberRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class MemberLoginService extends BaseUserService {
    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private MemberRepository memberRepository;

    public Map<String, Object> registerMember(String email, String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 創建基本用戶並獲取 user_id
            UsersBean user = createBaseUser(email, password, UsersBean.UserRole.MEMBER);
            
            // 創建會員資料
            MemberBean member = new MemberBean();
            member.setId(user.getId());  // 使用相同的 ID
            member.setStatus(false);     // 預設未驗證
            member.setUpdatedDate(LocalDateTime.now());
            
            // 保存會員資料
            MemberBean savedMember = memberRepository.save(member);
            
            // 發送驗證郵件
            emailService.sendVerificationEmail(email, user.getVerificationToken());
            
            result.put("success", true);
            result.put("userId", user.getId());
            result.put("memberId", savedMember.getId());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }

    public class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
    }

    // 登入時驗證密碼
    public UsersBean memberLogin(String email, String password) {
        UsersBean user = findByEmail(email);
        if (user == null || user.getUserRole() != UsersBean.UserRole.MEMBER) {
            return null;
        }
        return passwordEncoder.matches(password, user.getPassword()) ? user : null;
    }

    public UsersBean findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UsersBean findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
} 