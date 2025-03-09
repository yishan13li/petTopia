package petTopia.service.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.time.LocalDateTime;
import java.util.Random;

import petTopia.model.user.Users;
import petTopia.model.user.Member;
import petTopia.repository.user.UsersRepository;
import petTopia.repository.user.MemberRepository;

@Service
@Transactional
public class MemberLoginService extends BaseUserService {
    private static final Logger logger = LoggerFactory.getLogger(MemberLoginService.class);

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void updateUser(Users user) {
        try {
            entityManager.merge(user);
            entityManager.flush();
        } catch (Exception e) {
            logger.error("更新用戶信息失敗", e);
            throw e;
        }
    }

    @Transactional
    public Map<String, Object> registerMember(Users user, Member member) {
        Map<String, Object> result = new HashMap<>();
        logger.info("開始會員註冊流程，email: {}", user.getEmail());

        try {
            // 基本輸入驗證
            if (user.getEmail() == null || user.getEmail().trim().isEmpty()) {
                logger.warn("註冊失敗：電子郵件為空");
                result.put("success", false);
                result.put("message", "請輸入電子郵件地址");
                result.put("errorCode", "EMAIL_EMPTY");
                return result;
            }

            if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
                logger.warn("註冊失敗：密碼為空");
                result.put("success", false);
                result.put("message", "請輸入密碼");
                result.put("errorCode", "PASSWORD_EMPTY");
                return result;
            }

            // 驗證郵件格式
            if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                logger.warn("註冊失敗：郵件格式不正確，email: {}", user.getEmail());
                result.put("success", false);
                result.put("message", "請輸入有效的電子郵件地址");
                result.put("errorCode", "INVALID_EMAIL_FORMAT");
                return result;
            }

            // 檢查信箱是否已被使用
            Users existingUser = usersRepository.findByEmail(user.getEmail());
            
            if (existingUser != null) {
                // 如果已經是會員帳號，不允許重複註冊（無論是Google還是本地註冊）
                if (existingUser.getUserRole() == Users.UserRole.MEMBER) {
                    logger.warn("註冊失敗：會員帳號已存在，email: {}", user.getEmail());
                    result.put("success", false);
                    if (existingUser.getProvider() == Users.Provider.GOOGLE) {
                        result.put("message", "此Email已透過Google登入註冊，請使用Google登入");
                        result.put("errorCode", "GOOGLE_ACCOUNT_EXISTS");
                    } else {
                        result.put("message", "此Email已註冊為會員帳號");
                        result.put("errorCode", "EMAIL_EXISTS");
                    }
                    return result;
                }
                
                // 如果是商家帳號，允許註冊會員帳號
                Users newMemberUser = new Users();
                newMemberUser.setEmail(user.getEmail());
                newMemberUser.setPassword(passwordEncoder.encode(user.getPassword()));
                newMemberUser.setProvider(Users.Provider.LOCAL);
                newMemberUser.setUserRole(Users.UserRole.MEMBER);
                newMemberUser.setEmailVerified(false);
                
                // 生成6位數驗證碼
                String verificationCode = generateVerificationCode();
                newMemberUser.setVerificationToken(verificationCode);
                newMemberUser.setTokenExpiry(LocalDateTime.now().plusMinutes(10));
                
                try {
                    // 儲存新的用戶資訊
                    entityManager.persist(newMemberUser);
                    entityManager.flush();
                    logger.info("會員用戶資訊儲存成功，userId: {}", newMemberUser.getId());
                } catch (Exception e) {
                    logger.error("會員用戶資訊儲存失敗", e);
                    result.put("success", false);
                    result.put("message", "註冊失敗：用戶資訊儲存異常");
                    result.put("errorCode", "USER_SAVE_ERROR");
                    return result;
                }
                
                // 設置會員資訊
                Member newMember = new Member();
                newMember.setId(newMemberUser.getId());
                newMember.setUser(newMemberUser);
                newMember.setStatus(false);
                newMember.setGender(false);
                newMember.setUpdatedDate(LocalDateTime.now());
                if (member.getName() != null) {
                    newMember.setName(member.getName());
                }
                
                try {
                    // 儲存會員資訊
                    entityManager.persist(newMember);
                    entityManager.flush();
                    logger.info("會員詳細資訊儲存成功，memberId: {}", newMember.getId());
                } catch (Exception e) {
                    logger.error("會員詳細資訊儲存失敗", e);
                    result.put("success", false);
                    result.put("message", "註冊失敗：會員資訊儲存異常");
                    result.put("errorCode", "MEMBER_SAVE_ERROR");
                    return result;
                }
                
                try {
                    // 發送驗證碼郵件
                    sendVerificationEmail(user.getEmail(), verificationCode);
                    logger.info("驗證碼郵件發送成功，email: {}", user.getEmail());
                } catch (Exception e) {
                    logger.error("驗證碼郵件發送失敗", e);
                    result.put("success", false);
                    result.put("message", "註冊成功但驗證郵件發送失敗，請稍後重試驗證郵件發送");
                    result.put("errorCode", "EMAIL_SEND_ERROR");
                    result.put("userId", newMemberUser.getId());
                    return result;
                }
                
                result.put("success", true);
                result.put("message", "註冊成功，請查收驗證碼郵件");
                result.put("userId", newMemberUser.getId());
                logger.info("會員註冊完成，userId: {}", newMemberUser.getId());
                
                return result;
            }

            // 如果是全新的email註冊
            user.setProvider(Users.Provider.LOCAL);
            user.setUserRole(Users.UserRole.MEMBER);
            user.setEmailVerified(false);
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // 生成6位數驗證碼
            String verificationCode = generateVerificationCode();
            user.setVerificationToken(verificationCode);
            user.setTokenExpiry(LocalDateTime.now().plusMinutes(10));

            try {
                // 儲存使用者資訊
                entityManager.persist(user);
                entityManager.flush();
                logger.info("會員用戶資訊儲存成功，userId: {}", user.getId());
            } catch (Exception e) {
                logger.error("會員用戶資訊儲存失敗", e);
                result.put("success", false);
                result.put("message", "註冊失敗：用戶資訊儲存異常");
                result.put("errorCode", "USER_SAVE_ERROR");
                return result;
            }

            // 設置會員資訊
            member.setId(user.getId());
            member.setUser(user);
            member.setStatus(false);
            member.setGender(false);
            member.setUpdatedDate(LocalDateTime.now());

            try {
                // 儲存會員資訊
                entityManager.persist(member);
                entityManager.flush();
                logger.info("會員詳細資訊儲存成功，memberId: {}", member.getId());
            } catch (Exception e) {
                logger.error("會員詳細資訊儲存失敗", e);
                result.put("success", false);
                result.put("message", "註冊失敗：會員資訊儲存異常");
                result.put("errorCode", "MEMBER_SAVE_ERROR");
                return result;
            }

            try {
                // 發送驗證碼郵件
                sendVerificationEmail(user.getEmail(), verificationCode);
                logger.info("驗證碼郵件發送成功，email: {}", user.getEmail());
            } catch (Exception e) {
                logger.error("驗證碼郵件發送失敗", e);
                result.put("success", false);
                result.put("message", "註冊成功但驗證郵件發送失敗，請稍後重試驗證郵件發送");
                result.put("errorCode", "EMAIL_SEND_ERROR");
                result.put("userId", user.getId());
                return result;
            }

            result.put("success", true);
            result.put("message", "註冊成功，請查收驗證碼郵件");
            result.put("userId", user.getId());
            logger.info("會員註冊完成，userId: {}", user.getId());

        } catch (Exception e) {
            logger.error("會員註冊發生未預期的異常", e);
            result.put("success", false);
            result.put("message", "系統發生錯誤，請稍後再試或聯繫客服");
            result.put("errorCode", "SYSTEM_ERROR");
        }

        return result;
    }

    public Map<String, Object> memberLogin(String email, String password) {
        Map<String, Object> result = new HashMap<>();
        logger.info("開始會員登入流程，email: {}", email);

        try {
            // 輸入驗證
            if (email == null || email.trim().isEmpty()) {
                logger.warn("登入失敗：電子郵件為空");
                result.put("success", false);
                result.put("message", "請輸入電子郵件地址");
                result.put("errorCode", "EMAIL_EMPTY");
                return result;
            }

            if (password == null || password.trim().isEmpty()) {
                logger.warn("登入失敗：密碼為空");
                result.put("success", false);
                result.put("message", "請輸入密碼");
                result.put("errorCode", "PASSWORD_EMPTY");
                return result;
            }

            // 查找用戶
            logger.info("開始查找會員用戶，email: {}", email);
            Users user = usersRepository.findByEmailAndUserRole(email, Users.UserRole.MEMBER);
            
            if (user == null) {
                logger.warn("登入失敗：找不到會員帳號，email: {}", email);
                result.put("success", false);
                result.put("message", "此電子郵件尚未註冊，請先註冊會員");
                result.put("errorCode", "ACCOUNT_NOT_FOUND");
                return result;
            }
            
            logger.info("找到用戶，檢查帳號類型，userId: {}, provider: {}", user.getId(), user.getProvider());

            // 檢查密碼
            logger.info("開始驗證密碼，userId: {}", user.getId());
            if (!passwordEncoder.matches(password, user.getPassword())) {
                logger.warn("登入失敗：密碼錯誤，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "密碼錯誤，請重新輸入");
                result.put("errorCode", "INVALID_PASSWORD");
                return result;
            }
            logger.info("密碼驗證成功，userId: {}", user.getId());

            // 檢查郵箱驗證狀態
            logger.info("檢查郵箱驗證狀態，userId: {}, isEmailVerified: {}", user.getId(), user.isEmailVerified());
            if (!user.isEmailVerified()) {
                logger.warn("登入失敗：郵箱未驗證，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "請先完成郵箱驗證再登入");
                result.put("errorCode", "EMAIL_NOT_VERIFIED");
                result.put("needVerification", true);
                return result;
            }

            // 獲取會員信息
            logger.info("開始獲取會員信息，userId: {}", user.getId());
            Member member = memberRepository.findByUserIdWithJoin(user.getId())
                .orElse(null);
            
            if (member == null) {
                logger.error("登入失敗：會員信息不存在，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "會員資料不完整，請聯繫客服");
                result.put("errorCode", "MEMBER_INFO_NOT_FOUND");
                return result;
            }
            
            logger.info("會員信息獲取成功，memberId: {}, status: {}", member.getId(), member.getStatus());

            // 檢查會員狀態
            if (!member.getStatus()) {
                logger.warn("登入失敗：會員狀態未啟用，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "會員帳號尚未啟用，請完成驗證");
                result.put("errorCode", "ACCOUNT_NOT_ACTIVATED");
                return result;
            }

            // 登入成功
            result.put("success", true);
            result.put("message", "登入成功！");
            result.put("userId", user.getId());
            result.put("memberName", member.getName());
            result.put("userRole", user.getUserRole());
            logger.info("會員登入成功，userId: {}, memberName: {}", user.getId(), member.getName());

        } catch (Exception e) {
            logger.error("會員登入過程發生異常：", e);
            result.put("success", false);
            result.put("message", "系統發生錯誤，請稍後再試");
            result.put("errorCode", "SYSTEM_ERROR");
        }

        return result;
    }

    public class AuthenticationException extends RuntimeException {
        public AuthenticationException(String message) {
            super(message);
        }
    }

    @Override
    public Users findByEmail(String email) {
        return usersRepository.findByEmailAndUserRole(email, Users.UserRole.MEMBER);
    }

    public Users findById(Integer id) {
        return usersRepository.findById(id).orElse(null);
    }

    // 生成6位數驗證碼
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    // 發送驗證碼郵件
    private void sendVerificationEmail(String email, String code) {
        try {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.starttls.required", "true");
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
            props.put("mail.smtp.timeout", "5000");
            props.put("mail.smtp.connectiontimeout", "5000");
            props.put("mail.smtp.writetimeout", "5000");

            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("kh.film01232@gmail.com", "您的應用程式密碼");
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("kh.film01232@gmail.com", "Waggy寵物平台"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email));
            message.setSubject("Waggy會員驗證碼");
            
            String content = String.format(
                "親愛的會員您好，\n\n" +
                "感謝您註冊成為Waggy的會員！\n" +
                "您的驗證碼是：%s\n\n" +
                "請在10分鐘內完成驗證。\n" +
                "如果您沒有進行此操作，請忽略此郵件。\n\n" +
                "祝您使用愉快！\n" +
                "Waggy團隊", code);
            
            message.setText(content);
            
            logger.info("準備發送驗證碼郵件到: {}", email);
            Transport.send(message);
            logger.info("驗證碼郵件發送成功，email: {}", email);
            
        } catch (Exception e) {
            logger.error("發送驗證碼郵件失敗: {}", e.getMessage(), e);
            throw new RuntimeException("發送驗證碼郵件失敗: " + e.getMessage());
        }
    }

    // 驗證郵箱
    @Transactional
    public Map<String, Object> verifyEmail(String email, String code) {
        Map<String, Object> result = new HashMap<>();
        logger.info("開始會員郵箱驗證流程，email: {}", email);

        try {
            // 輸入驗證
            if (email == null || email.trim().isEmpty()) {
                logger.warn("驗證失敗：電子郵件為空");
                result.put("success", false);
                result.put("message", "請輸入電子郵件地址");
                result.put("errorCode", "EMAIL_EMPTY");
                return result;
            }

            if (code == null || code.trim().isEmpty()) {
                logger.warn("驗證失敗：驗證碼為空");
                result.put("success", false);
                result.put("message", "請輸入驗證碼");
                result.put("errorCode", "CODE_EMPTY");
                return result;
            }

            // 查找用戶
            logger.info("開始查找用戶，email: {}", email);
            Users user = usersRepository.findByEmail(email);
            if (user == null) {
                logger.warn("驗證失敗：找不到用戶，email: {}", email);
                result.put("success", false);
                result.put("message", "找不到對應的用戶");
                result.put("errorCode", "USER_NOT_FOUND");
                return result;
            }
            logger.info("找到用戶，userId: {}, 當前驗證狀態: {}", user.getId(), user.isEmailVerified());

            // 驗證碼檢查
            logger.info("開始驗證碼檢查，用戶驗證碼: {}, 輸入驗證碼: {}", user.getVerificationToken(), code);
            if (!code.equals(user.getVerificationToken())) {
                logger.warn("驗證失敗：驗證碼錯誤，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "驗證碼錯誤，請重新確認");
                result.put("errorCode", "INVALID_CODE");
                return result;
            }

            // 檢查過期時間
            if (user.getTokenExpiry() != null && user.getTokenExpiry().isBefore(LocalDateTime.now())) {
                logger.warn("驗證失敗：驗證碼已過期，userId: {}, 過期時間: {}", user.getId(), user.getTokenExpiry());
                result.put("success", false);
                result.put("message", "驗證碼已過期，請重新發送");
                result.put("errorCode", "CODE_EXPIRED");
                return result;
            }

            // 更新用戶驗證狀態
            user.setEmailVerified(true);
            user.setVerificationToken(null);
            user.setTokenExpiry(null);
            
            // 更新會員狀態
            logger.info("開始查找會員信息，userId: {}", user.getId());
            Member member = memberRepository.findByUserId(user.getId()).orElse(null);
            if (member == null) {
                logger.error("找不到對應的會員資訊，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "驗證失敗：找不到對應的會員資訊");
                result.put("errorCode", "MEMBER_NOT_FOUND");
                return result;
            }

            logger.info("找到會員信息，當前狀態: {}", member.getStatus());
            member.setStatus(true);
            member.setUpdatedDate(LocalDateTime.now());

            try {
                // 使用單一事務更新兩個實體
                entityManager.merge(user);
                entityManager.merge(member);
                entityManager.flush();
                
                // 重新查詢確認狀態
                member = memberRepository.findByUserId(user.getId()).orElse(null);
                user = usersRepository.findById(user.getId()).orElse(null);
                
                logger.info("驗證狀態更新完成 - 用戶狀態: emailVerified={}, 會員狀態: status={}", 
                    user != null ? user.isEmailVerified() : "未知",
                    member != null ? member.getStatus() : "未知");
                    
                if (member == null || !member.getStatus() || user == null || !user.isEmailVerified()) {
                    throw new RuntimeException("狀態更新失敗");
                }
            } catch (Exception e) {
                logger.error("狀態更新失敗", e);
                result.put("success", false);
                result.put("message", "驗證失敗：狀態更新異常");
                result.put("errorCode", "UPDATE_ERROR");
                return result;
            }

            result.put("success", true);
            result.put("message", "郵箱驗證成功！您現在可以登入系統了");
            logger.info("會員郵箱驗證完成，userId: {}", user.getId());
            
        } catch (Exception e) {
            logger.error("郵箱驗證發生未預期的異常", e);
            result.put("success", false);
            result.put("message", "系統發生錯誤，請稍後再試或聯繫客服");
            result.put("errorCode", "SYSTEM_ERROR");
        }
        return result;
    }
}