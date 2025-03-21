package petTopia.service.user;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import petTopia.model.user.User;
import petTopia.model.user.Member;
import petTopia.repository.user.UserRepository;
import petTopia.repository.user.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class RegistrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(RegistrationService.class);
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Autowired
    private UserRepository usersRepository;
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public Map<String, Object> register(User user) {
        Map<String, Object> result = new HashMap<>();
        String email = user.getEmail().toLowerCase().trim();
        
        try {
            // 檢查郵箱是否已存在
            if (findByEmail(email) != null) {
                result.put("success", false);
                result.put("message", "此 email 已註冊為會員");
                return result;
            }
            
            user.setEmail(email);
            
            // 生成6位數驗證碼
            String code = String.format("%06d", new Random().nextInt(1000000));
            user.setVerificationToken(code);
            user.setTokenExpiry(LocalDateTime.now().plusMinutes(5));
            user.setUserRole(User.UserRole.MEMBER);
            user.setProvider(User.Provider.LOCAL);
            
            // 加密密碼
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            
            // 使用 EntityManager 保存用戶
            entityManager.persist(user);
            entityManager.flush();
            logger.info("會員用戶資訊儲存成功，userId: {}", user.getId());
            
            // 創建會員資料
            Member member = new Member();
            member.setId(user.getId());
            member.setUser(user);
            member.setName("");
            member.setPhone("");
            member.setStatus(false);
            member.setUpdatedDate(LocalDateTime.now());
            
            // 使用 EntityManager 保存會員資料
            entityManager.persist(member);
            entityManager.flush();
            logger.info("會員詳細資訊儲存成功，memberId: {}", member.getId());

            // 發送驗證郵件
            emailService.sendVerificationEmail(user.getEmail(), code);
            logger.info("驗證郵件發送成功，email: {}", user.getEmail());

            result.put("success", true);
            result.put("message", "註冊成功，請查收驗證郵件");
            result.put("userId", user.getId());
            result.put("memberId", member.getId());

        } catch (Exception e) {
            logger.error("會員註冊過程發生錯誤", e);
            result.put("success", false);
            result.put("message", "註冊失敗：" + e.getMessage());
            throw new RuntimeException("會員註冊失敗", e);
        }

        return result;
    }
    
    @Transactional
    public boolean verifyEmail(String code) {
        User user = usersRepository.findByVerificationToken(code);
        
        if (user != null && !user.isEmailVerified() && 
            LocalDateTime.now().isBefore(user.getTokenExpiry())) {
            
            // 更新用戶驗證狀態
            user.setEmailVerified(true);
            user.setVerificationToken(null);
            user.setTokenExpiry(null);
            usersRepository.save(user);
            
            // 更新會員狀態
            Member member = memberRepository.findByUserId(user.getId()).orElse(null);
            if (member != null) {
                member.setStatus(true);
                memberRepository.save(member);
                logger.info("會員驗證完成，userId: {}", user.getId());
                return true;
            }
        }
        return false;
    }

    public User findByEmail(String email) {
        // 查找任何類型的帳號（會員、商家、本地、Google）
        return usersRepository.findByEmailAndUserRole(email.toLowerCase().trim(), User.UserRole.MEMBER);
    }
    
    @Transactional
    public User updateUser(User user) {
        // 保存用戶信息
        logger.info("更新用戶信息，userId: {}", user.getId());
        return usersRepository.save(user);
    }
}
