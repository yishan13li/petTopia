package petTopia.service.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import petTopia.model.user.UsersBean;
import petTopia.repository.user.UsersRepository;

@Service
public class RegistrationService {
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public void register(UsersBean user) {
        // 生成驗證令牌
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(24));
        
        // 加密密碼
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        // 保存用戶
        usersRepository.save(user);
        
        // 發送驗證郵件
        emailService.sendVerificationEmail(user.getEmail(), token);
    }
    
    @Transactional
    public boolean verifyEmail(String token) {
        UsersBean user = usersRepository.findByVerificationToken(token);
        
        if (user != null && !user.isEmailVerified() && 
            LocalDateTime.now().isBefore(user.getTokenExpiry())) {
            user.setEmailVerified(true);
            user.setVerificationToken(null);
            user.setTokenExpiry(null);
            usersRepository.save(user);
            return true;
        }
        return false;
    }

    public UsersBean registerUser(String email, String password) {
        // 加密密碼
        String encodedPassword = passwordEncoder.encode(password);
        
        UsersBean user = new UsersBean();
        user.setEmail(email);
        user.setPassword(encodedPassword);  // 存儲加密後的密碼
        // ...
        return user;
    }
} 