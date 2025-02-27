package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import petTopia.model.user.UsersBean;
import petTopia.repository.user.UsersRepository;


@Service
@Transactional
public class MemberLoginService {
    @Autowired
    private UsersRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 註冊時加密密碼
    public UsersBean register(UsersBean user) {
        // 檢查郵箱是否已存在
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalStateException("此電子郵件已被註冊");
        }

        // 加密密碼
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        // 設置為會員角色
        user.setUserRole(UsersBean.UserRole.MEMBER);
        
        return userRepository.save(user);
    }

    public class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
    }

    // 登入時驗證密碼
    public UsersBean memberLogin(String email, String password) {
        if (email == null || password == null) {
            throw new IllegalArgumentException("Email和密碼不能為空");
        }

        UsersBean user = userRepository.findByEmail(email);
        if (user == null) {
            throw new AuthenticationException("用戶不存在");
        }
        
        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        throw new AuthenticationException("密碼錯誤");
    }

    public UsersBean findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public UsersBean findById(Integer id) {
        return userRepository.findById(id).orElse(null);
    }
} 