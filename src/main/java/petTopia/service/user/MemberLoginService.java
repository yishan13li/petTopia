package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import petTopia.model.user.UsersBean;
import petTopia.repository.user.UserRepository;
import java.util.Optional;

@Service
@Transactional
public class MemberLoginService {
    @Autowired
    private UserRepository userRepository;

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

    // 登入時驗證密碼
    public UsersBean memberLogin(String email, String password) {
        try {
            // 使用新的方法獲取用戶
            Optional<UsersBean> userOpt = userRepository.findFirstByEmailOrderByIdDesc(email);
            
            if (userOpt.isPresent()) {
                UsersBean user = userOpt.get();
                // 驗證密碼
                if (password.equals(user.getPassword())) {
                    return user;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public UsersBean findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
} 