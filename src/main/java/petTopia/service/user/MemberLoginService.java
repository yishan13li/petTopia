package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private UsersRepository usersRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Map<String, Object> registerMember(UsersBean user, MemberBean member) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 檢查是否已註冊為會員
            UsersBean existingMember = usersRepository.findByEmailAndUserRole(
                user.getEmail(), 
                UsersBean.UserRole.MEMBER
            );
            
            if (existingMember != null) {
                result.put("success", false);
                result.put("message", "此 email 已註冊為會員");
                return result;
            }

            // 2. 加密密碼
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // 3. 保存用戶信息
            UsersBean savedUser = usersRepository.save(user);
            
            // 4. 設置會員信息
            member.setId(savedUser.getId());
            member.setUser(savedUser);
            
            // 5. 保存會員信息
            memberRepository.save(member);
            
            result.put("success", true);
            result.put("message", "註冊成功");
            result.put("userId", savedUser.getId());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "註冊失敗：" + e.getMessage());
        }
        
        return result;
    }

    public Map<String, Object> memberLogin(String email, String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            UsersBean user = usersRepository.findByEmailAndUserRole(email, UsersBean.UserRole.MEMBER);
            
            if (user == null) {
                result.put("success", false);
                result.put("message", "會員帳號不存在");
                return result;
            }
            
            if (!passwordEncoder.matches(password, user.getPassword())) {
                result.put("success", false);
                result.put("message", "密碼錯誤");
                return result;
            }
            
            result.put("success", true);
            result.put("message", "登入成功");
            result.put("user", user);
            
        } catch (Exception e) {
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

    public UsersBean findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }

    public UsersBean findById(Integer id) {
        return usersRepository.findById(id).orElse(null);
    }
} 