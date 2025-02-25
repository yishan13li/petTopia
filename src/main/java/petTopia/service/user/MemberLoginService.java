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
    private UsersRepository usersRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    // 註冊時加密密碼
    public UsersBean register(UsersBean user) {
        // 檢查郵箱是否已存在
        if (usersRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalStateException("此電子郵件已被註冊");
        }

        // 加密密碼
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        // 設置為會員角色
        user.setUserRole(UsersBean.UserRole.MEMBER);
        
        return usersRepository.save(user);
    }

    // 登入時驗證密碼
    public UsersBean memberLogin(String email, String password) {
        UsersBean user = usersRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }

    public UsersBean findByEmail(String email) {
        return usersRepository.findByEmail(email);
    }
} 