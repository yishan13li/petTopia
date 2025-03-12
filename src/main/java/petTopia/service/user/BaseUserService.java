package petTopia.service.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import petTopia.model.user.Users;
import petTopia.repository.user.UsersRepository;

@Service
public abstract class BaseUserService {
    @Autowired
    protected UsersRepository usersRepository;

    @Autowired
    protected BCryptPasswordEncoder passwordEncoder;

    @Autowired
    protected EmailService emailService;

    protected Users createBaseUser(String email, String password, Users.UserRole role) {
        // 檢查郵箱是否已存在
        if (usersRepository.findByEmail(email) != null) {
            throw new IllegalStateException("此電子郵件已被註冊");
        }

        Users user = new Users();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setUserRole(role);

        // 生成驗證令牌
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(24));

        return usersRepository.save(user);
    }

    public Users findByEmail(String email) {
        return usersRepository.findByEmailAndUserRole(email, Users.UserRole.MEMBER);
    }
}