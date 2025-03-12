package petTopia.service.user;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import petTopia.model.user.Users;
import petTopia.model.user.Member;
import petTopia.repository.user.UsersRepository;
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
    private UsersRepository usersRepository;
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Transactional
    public Map<String, Object> register(Users user) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            String email = user.getEmail().toLowerCase().trim();  // 統一轉換為小寫並去除空格
            logger.info("開始檢查 email 是否已存在：{}", email);
            
            // 檢查是否已存在相同email的帳號
            Users existingUser = usersRepository.findByEmailAndUserRole(email, Users.UserRole.MEMBER);
            
            if (existingUser != null) {
                String message;
                if (existingUser.getProvider() == Users.Provider.GOOGLE) {
                    message = "此 email 已使用 Google 帳號登入過，請點擊「使用 Google 登入」按鈕";
                    logger.warn("註冊失敗：使用已存在的 Google 帳號 email 嘗試本地註冊，email: {}, provider: {}", 
                        email, existingUser.getProvider());
                } else if (existingUser.getUserRole() == Users.UserRole.VENDOR) {
                    message = "此 email 已註冊為商家帳號，請使用其他 email 註冊會員";
                    logger.warn("註冊失敗：使用已存在的商家帳號 email 嘗試會員註冊，email: {}, role: {}", 
                        email, existingUser.getUserRole());
                } else {
                    message = "此 email 已註冊為會員帳號，請直接登入";
                    logger.warn("註冊失敗：使用已存在的會員帳號 email 嘗試重複註冊，email: {}, role: {}", 
                        email, existingUser.getUserRole());
                }
                result.put("success", false);
                result.put("message", message);
                return result;
            }

            // 只有在 email 不存在時才繼續註冊流程
            logger.info("Email 檢查通過，開始新會員註冊流程，email: {}", email);
            
            // 設置 email 為小寫
            user.setEmail(email);
            
            // 生成驗證令牌
            String token = UUID.randomUUID().toString();
            user.setVerificationToken(token);
            user.setTokenExpiry(LocalDateTime.now().plusHours(24));
            user.setUserRole(Users.UserRole.MEMBER);
            user.setProvider(Users.Provider.LOCAL);
            
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
            emailService.sendVerificationEmail(user.getEmail(), token);
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
    public boolean verifyEmail(String token) {
        Users user = usersRepository.findByVerificationToken(token);
        
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

    public Users findByEmail(String email) {
        // 查找任何類型的帳號（會員、商家、本地、Google）
        return usersRepository.findByEmailAndUserRole(email.toLowerCase().trim(), Users.UserRole.MEMBER);
    }
}
