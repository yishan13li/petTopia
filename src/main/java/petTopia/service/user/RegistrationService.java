package petTopia.service.user;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import petTopia.model.user.Users;
import petTopia.model.user.Member;  // 確保這裡有引入 Member 類別
import petTopia.repository.user.UsersRepository;
import petTopia.repository.user.MemberRepository;

@Service
public class RegistrationService {
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public void register(Users user) {
        // 生成驗證令牌
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setTokenExpiry(LocalDateTime.now().plusHours(24));
        
        // 加密密碼
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        
        // 保存用戶
        usersRepository.save(user); // 保存 Users，並獲得 id
        
        // 創建並保存 Member，並將 Member 的 id 設為用戶的 id
        Member member = new Member();
        member.setId(user.getId());  // 設定 Member 的 id 與 Users 的 id 相同
        member.setName("");  // 預設名稱，可以根據需要設置
        member.setPhone("");  // 預設電話號碼，可以根據需要設置
        member.setStatus(false);  // 預設為未認證
        member.setUpdatedDate(LocalDateTime.now());

        // 保存 Member
        memberRepository.save(member);

        // 發送驗證郵件
        emailService.sendVerificationEmail(user.getEmail(), token);
    }
    
    @Transactional
    public boolean verifyEmail(String token) {
        Users user = usersRepository.findByVerificationToken(token);
        
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

    public Users registerUser(String email, String password) {
        // 加密密碼
        String encodedPassword = passwordEncoder.encode(password);
        
        Users user = new Users();
        user.setEmail(email);
        user.setPassword(encodedPassword);  // 存儲加密後的密碼
        // ...
        return user;
    }
}
