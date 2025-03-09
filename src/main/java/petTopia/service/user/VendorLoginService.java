package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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

import org.springframework.security.crypto.password.PasswordEncoder;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.time.LocalDateTime;
import java.util.Random;

import petTopia.model.user.Users;
import petTopia.repository.user.UsersRepository;
import petTopia.model.user.Vendor;
import petTopia.repository.user.VendorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
@Service
@Transactional
public class VendorLoginService extends BaseUserService {
    private static final Logger logger = LoggerFactory.getLogger(VendorLoginService.class);

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private VendorRepository vendorRepository;

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

    @Override
    public Users findByEmail(String email) {
        return usersRepository.findByEmailAndUserRole(email, Users.UserRole.VENDOR);
    }

    @Transactional
    public Map<String, Object> registerVendor(Users user, Vendor vendor) {
        Map<String, Object> result = new HashMap<>();
        logger.info("開始商家註冊流程，email: {}", user.getEmail());

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
            Users existingVendor = usersRepository.findByEmailAndUserRole(user.getEmail(), Users.UserRole.VENDOR);
            
            if (existingVendor != null) {
                logger.warn("註冊失敗：商家帳號已存在，email: {}", user.getEmail());
                result.put("success", false);
                result.put("message", "此Email已註冊為商家帳號");
                result.put("errorCode", "EMAIL_EXISTS");
                return result;
            }
                
            // 如果是會員帳號或新帳號，允許註冊商家帳號
            Users newVendorUser = new Users();
            newVendorUser.setEmail(user.getEmail());
            newVendorUser.setPassword(passwordEncoder.encode(user.getPassword()));
            newVendorUser.setProvider(Users.Provider.LOCAL);
            newVendorUser.setUserRole(Users.UserRole.VENDOR);
            newVendorUser.setEmailVerified(false);
            
            // 生成6位數驗證碼
            String verificationCode = generateVerificationCode();
            newVendorUser.setVerificationToken(verificationCode);
            newVendorUser.setTokenExpiry(LocalDateTime.now().plusMinutes(10));
            
            try {
                // 儲存新的用戶資訊
                entityManager.persist(newVendorUser);
                entityManager.flush();
                logger.info("商家用戶資訊儲存成功，userId: {}", newVendorUser.getId());
            } catch (Exception e) {
                logger.error("商家用戶資訊儲存失敗", e);
                result.put("success", false);
                result.put("message", "註冊失敗：用戶資訊儲存異常");
                result.put("errorCode", "USER_SAVE_ERROR");
                return result;
            }
            
            // 設置商家資訊
            Vendor newVendor = new Vendor();
            newVendor.setId(newVendorUser.getId());
            newVendor.setUser(newVendorUser);
            newVendor.setRegistrationDate(LocalDateTime.now());
            newVendor.setUpdatedDate(LocalDateTime.now());
            newVendor.setStatus(false);  // 預設為未驗證
            newVendor.setVendorCategoryId(vendor.getVendorCategoryId());
            if (vendor.getName() != null) {
                newVendor.setName(vendor.getName());
            }
            
            try {
                // 儲存商家資訊
                entityManager.persist(newVendor);
                entityManager.flush();
                logger.info("商家詳細資訊儲存成功，vendorId: {}", newVendor.getId());
            } catch (Exception e) {
                logger.error("商家詳細資訊儲存失敗", e);
                result.put("success", false);
                result.put("message", "註冊失敗：商家資訊儲存異常");
                result.put("errorCode", "VENDOR_SAVE_ERROR");
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
                result.put("userId", newVendorUser.getId());
                return result;
            }
            
            result.put("success", true);
            result.put("message", "註冊成功，請查收驗證碼郵件");
            result.put("userId", newVendorUser.getId());
            logger.info("商家註冊完成，userId: {}", newVendorUser.getId());

        } catch (Exception e) {
            logger.error("商家註冊發生未預期的異常", e);
            result.put("success", false);
            result.put("message", "系統發生錯誤，請稍後再試或聯繫客服");
            result.put("errorCode", "SYSTEM_ERROR");
        }

        return result;
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
            message.setSubject("Waggy商家驗證碼");
            
            String content = String.format(
                "親愛的商家夥伴您好，\n\n" +
                "感謝您註冊成為Waggy的合作商家！\n" +
                "您的驗證碼是：%s\n\n" +
                "請在10分鐘內完成驗證。\n" +
                "如果您沒有進行此操作，請忽略此郵件。\n\n" +
                "祝您生意興隆！\n" +
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
        logger.info("開始商家郵箱驗證流程，email: {}", email);

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

            Users user = usersRepository.findByEmail(email);
            if (user == null) {
                logger.warn("驗證失敗：找不到用戶，email: {}", email);
                result.put("success", false);
                result.put("message", "找不到對應的用戶");
                result.put("errorCode", "USER_NOT_FOUND");
                return result;
            }

            if (!code.equals(user.getVerificationToken())) {
                logger.warn("驗證失敗：驗證碼錯誤，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "驗證碼錯誤，請重新確認");
                result.put("errorCode", "INVALID_CODE");
                return result;
            }

            if (user.getTokenExpiry().isBefore(LocalDateTime.now())) {
                logger.warn("驗證失敗：驗證碼已過期，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "驗證碼已過期，請重新發送");
                result.put("errorCode", "CODE_EXPIRED");
                return result;
            }

            user.setEmailVerified(true);
            user.setVerificationToken(null);
            user.setTokenExpiry(null);
            
            try {
                // 更新用戶資訊
                entityManager.merge(user);
                logger.info("用戶驗證狀態更新成功，userId: {}", user.getId());
            } catch (Exception e) {
                logger.error("用戶驗證狀態更新失敗", e);
                result.put("success", false);
                result.put("message", "驗證失敗：用戶狀態更新異常");
                result.put("errorCode", "USER_UPDATE_ERROR");
                return result;
            }

            // 更新商家狀態
            Vendor vendor = vendorRepository.findByUserId(user.getId()).orElse(null);
            if (vendor != null) {
                vendor.setStatus(true);
                try {
                    entityManager.merge(vendor);
                    logger.info("商家狀態更新成功，userId: {}", user.getId());
                } catch (Exception e) {
                    logger.error("商家狀態更新失敗", e);
                    result.put("success", false);
                    result.put("message", "驗證失敗：商家狀態更新異常");
                    result.put("errorCode", "VENDOR_UPDATE_ERROR");
                    return result;
                }
            } else {
                logger.error("找不到對應的商家資訊，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "驗證失敗：找不到對應的商家資訊");
                result.put("errorCode", "VENDOR_NOT_FOUND");
                return result;
            }

            try {
                entityManager.flush();
            } catch (Exception e) {
                logger.error("資料庫更新失敗", e);
                result.put("success", false);
                result.put("message", "驗證失敗：資料庫更新異常");
                result.put("errorCode", "DATABASE_ERROR");
                return result;
            }

            result.put("success", true);
            result.put("message", "郵箱驗證成功！您現在可以登入系統了");
            logger.info("商家郵箱驗證完成，userId: {}", user.getId());
        } catch (Exception e) {
            logger.error("郵箱驗證發生未預期的異常", e);
            result.put("success", false);
            result.put("message", "系統發生錯誤，請稍後再試或聯繫客服");
            result.put("errorCode", "SYSTEM_ERROR");
        }
        return result;
    }

    public Map<String, Object> vendorLogin(String email, String password) {
        Map<String, Object> result = new HashMap<>();
        logger.info("開始商家登入流程，email: {}", email);

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

            // 驗證郵件格式
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                logger.warn("登入失敗：郵件格式不正確，email: {}", email);
                result.put("success", false);
                result.put("message", "請輸入有效的電子郵件地址");
                result.put("errorCode", "INVALID_EMAIL_FORMAT");
                return result;
            }

            // 查找用戶
            logger.info("開始查找商家用戶，email: {}", email);
            Users user = usersRepository.findByEmailAndUserRole(email, Users.UserRole.VENDOR);
            logger.info("查詢用戶結果: {}, 用戶角色: {}", 
                user != null ? "找到用戶" : "未找到用戶",
                user != null ? user.getUserRole() : "無");

            if (user == null) {
                // 檢查是否是會員帳號
                Users memberUser = usersRepository.findByEmailAndUserRole(email, Users.UserRole.MEMBER);
                if (memberUser != null) {
                    logger.warn("登入失敗：會員帳號嘗試登入商家系統，email: {}", email);
                    result.put("success", false);
                    result.put("message", "此帳號為會員帳號，請使用會員登入頁面");
                    result.put("errorCode", "MEMBER_ACCOUNT");
                    return result;
                }
                
                logger.warn("登入失敗：找不到商家帳號，email: {}", email);
                result.put("success", false);
                result.put("message", "此電子郵件尚未註冊，請先申請成為商家");
                result.put("errorCode", "ACCOUNT_NOT_FOUND");
                return result;
            }

            // 檢查是否是Google帳號
            logger.info("檢查帳號類型，Provider: {}", user.getProvider());
            if (user.getProvider() == Users.Provider.GOOGLE) {
                logger.warn("登入失敗：Google帳號嘗試使用密碼登入，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "此帳號是使用Google註冊的，請點擊「使用Google登入」按鈕");
                result.put("errorCode", "GOOGLE_ACCOUNT");
                return result;
            }

            // 檢查密碼
            logger.info("開始驗證密碼，userId: {}", user.getId());
            boolean passwordMatch = passwordEncoder.matches(password, user.getPassword());
            logger.info("密碼驗證結果: {}, userId: {}", passwordMatch ? "成功" : "失敗", user.getId());
            
            if (!passwordMatch) {
                logger.warn("登入失敗：密碼錯誤，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "密碼錯誤，請重新輸入");
                result.put("errorCode", "INVALID_PASSWORD");
                return result;
            }

            // 檢查郵箱驗證狀態
            logger.info("檢查郵箱驗證狀態，isEmailVerified: {}, userId: {}", user.isEmailVerified(), user.getId());
            if (!user.isEmailVerified()) {
                logger.warn("登入失敗：郵箱未驗證，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "您的郵箱尚未驗證，請查收驗證郵件並完成驗證");
                result.put("errorCode", "EMAIL_NOT_VERIFIED");
                result.put("needVerification", true);
                return result;
            }

            // 獲取商家信息
            logger.info("開始獲取商家信息，userId: {}", user.getId());
            Vendor vendor = vendorRepository.findByUserIdWithJoin(user.getId())
                .orElse(null);
            logger.info("查詢商家信息結果: {}, userId: {}", 
                vendor != null ? "找到商家信息" : "未找到商家信息", 
                user.getId());
            
            if (vendor == null) {
                logger.error("登入失敗：商家信息不存在，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "無法找到您的商家資料，請聯繫客服處理");
                result.put("errorCode", "VENDOR_INFO_NOT_FOUND");
                return result;
            }

            // 檢查商家狀態
            logger.info("檢查商家狀態，status: {}, userId: {}", vendor.getStatus(), user.getId());
            if (!vendor.getStatus()) {
                logger.warn("登入失敗：商家狀態未啟用，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "您的商家帳號尚未啟用，請完成郵箱驗證後再登入");
                result.put("errorCode", "ACCOUNT_NOT_ACTIVATED");
                result.put("needVerification", true);
                return result;
            }
            
            // 登入成功，設置返回信息
            result.put("success", true);
            result.put("message", "登入成功，歡迎回來！");
            result.put("user", user);
            result.put("userId", user.getId());
            result.put("vendorName", vendor.getName() != null ? vendor.getName() : "未設置名稱");
            result.put("userRole", user.getUserRole());
            result.put("loggedInUser", user);
            logger.info("商家登入成功，userId: {}, vendorName: {}, userRole: {}", 
                user.getId(), 
                vendor.getName() != null ? vendor.getName() : "未設置名稱",
                user.getUserRole());

        } catch (Exception e) {
            logger.error("商家登入發生異常，email: {}", email, e);
            result.put("success", false);
            result.put("message", "系統發生錯誤，請稍後再試或聯繫客服");
            result.put("errorCode", "SYSTEM_ERROR");
            // 輸出異常堆疊信息
            e.printStackTrace();
        }

        return result;
    }
}
