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
import java.util.Optional;
import java.util.Date;

import petTopia.model.user.User;
import petTopia.model.vendor.Vendor;
import petTopia.repository.user.UserRepository;
import petTopia.repository.vendor.VendorRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("unused")
@Service
@Transactional
public class VendorLoginService extends BaseUserService {
    private static final Logger logger = LoggerFactory.getLogger(VendorLoginService.class);

    @Autowired
    private UserRepository usersRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public void updateUser(User user) {
        try {
            entityManager.merge(user);
            entityManager.flush();
        } catch (Exception e) {
            logger.error("更新用戶信息失敗", e);
            throw e;
        }
    }

    @Override
    public User findByEmail(String email) {
        return usersRepository.findByEmailAndUserRole(email, User.UserRole.VENDOR);
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
                return result;
            }

            if (password == null || password.trim().isEmpty()) {
                logger.warn("登入失敗：密碼為空");
                result.put("success", false);
                result.put("message", "請輸入密碼");
                return result;
            }

            // 驗證郵件格式
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                logger.warn("登入失敗：郵件格式不正確，email: {}", email);
                result.put("success", false);
                result.put("message", "請輸入有效的電子郵件地址");
                return result;
            }

            // 查找用戶
            logger.info("開始查找商家用戶，email: {}", email);
            User user = usersRepository.findByEmailAndUserRole(email, User.UserRole.VENDOR);
            logger.info("查詢用戶結果: {}, 用戶角色: {}", 
                user != null ? "找到用戶" : "未找到用戶",
                user != null ? user.getUserRole() : "無");

            if (user == null) {
                // 檢查是否是會員帳號
                User memberUser = usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER);
                if (memberUser != null) {
                    logger.warn("登入失敗：會員帳號嘗試登入商家系統，email: {}", email);
                    result.put("success", false);
                    result.put("message", "此帳號為會員帳號，請使用會員登入頁面");
                    return result;
                }
                
                logger.warn("登入失敗：找不到商家帳號，email: {}", email);
                result.put("success", false);
                result.put("message", "此電子郵件尚未註冊，請先申請成為商家");
                return result;
            }

            // 檢查是否是Google帳號
            logger.info("檢查帳號類型，Provider: {}", user.getProvider());
            if (user.getProvider() == User.Provider.GOOGLE) {
                logger.warn("登入失敗：Google帳號嘗試使用密碼登入，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "此帳號是使用Google註冊的，請點擊「使用Google登入」按鈕");
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
                return result;
            }

            // 檢查郵箱驗證狀態
            logger.info("檢查郵箱驗證狀態，isEmailVerified: {}, userId: {}", user.isEmailVerified(), user.getId());
            if (!user.isEmailVerified()) {
                logger.warn("登入失敗：郵箱未驗證，userId: {}", user.getId());
                result.put("success", false);
                result.put("message", "您的郵箱尚未驗證，請查收驗證郵件並完成驗證");
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
            result.put("vendorStatus", vendor.isStatus());
            logger.info("商家登入成功，userId: {}, vendorName: {}, userRole: {}", 
                user.getId(), 
                vendor.getName() != null ? vendor.getName() : "未設置名稱",
                user.getUserRole());

        } catch (Exception e) {
            logger.error("商家登入發生異常，email: {}", email, e);
            result.put("success", false);
            result.put("message", "系統發生錯誤，請稍後再試或聯繫客服");
            // 輸出異常堆疊信息
            e.printStackTrace();
        }

        return result;
    }

    @Transactional
    public Map<String, Object> vendorOAuth2Login(String email) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 檢查是否有對應的商家帳號
            User vendor = usersRepository.findByEmailAndUserRole(email, User.UserRole.VENDOR);
            
            if (vendor == null) {
                result.put("success", false);
                result.put("message", "此Google帳號尚未註冊為商家");
                return result;
            }
            
            // 檢查商家狀態
            Optional<Vendor> vendorInfo = vendorRepository.findByUserId(vendor.getId());
            if (!vendorInfo.isPresent()) {
                result.put("success", false);
                result.put("message", "找不到商家資料");
                return result;
            }
            
            // 檢查email是否已驗證
            if (!vendor.isEmailVerified()) {
                result.put("success", false);
                result.put("message", "請先驗證您的電子郵件");
                return result;
            }
            
            // 登入成功
            result.put("success", true);
            result.put("user", vendor);
            result.put("userId", vendor.getId());
            result.put("vendorName", vendor.getEmail());
            result.put("userRole", vendor.getUserRole());
            result.put("vendorStatus", vendorInfo.get().isStatus());
            
            logger.info("OAuth2商家登入成功 - 使用者ID: {}", vendor.getId());
            
        } catch (Exception e) {
            logger.error("OAuth2商家登入過程發生錯誤", e);
            result.put("success", false);
            result.put("message", "系統發生錯誤，請稍後再試");
        }
        
        return result;
    }

    public Map<String, Object> getVendorInfo(Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        User user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
            
        if (user.getUserRole() != User.UserRole.VENDOR) {
            throw new RuntimeException("此帳號不是商家帳號");
        }
        
        Vendor vendor = vendorRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("商家資料不存在"));
        
        result.put("vendorId", vendor.getId());
        result.put("vendorName", vendor.getName());
        result.put("email", user.getEmail());
        result.put("phone", vendor.getPhone());
        result.put("address", vendor.getAddress());
        result.put("description", vendor.getDescription());
        result.put("category", vendor.getVendorCategory());
        result.put("status", vendor.isStatus());
        
        return result;
    }

    /**
     * 檢查用戶是否有未完成的商家申請
     */
    public boolean hasPendingVendorApplication(Integer userId) {
        try {
            // TODO: 實作檢查未完成申請的邏輯
            // 這裡需要根據您的資料庫結構來實作
            return false;
        } catch (Exception e) {
            logger.error("檢查未完成商家申請失敗", e);
            return false;
        }
    }

    /**
     * 檢查用戶是否有被拒絕的商家申請
     */
    public boolean hasRejectedVendorApplication(Integer userId) {
        try {
            // TODO: 實作檢查被拒絕申請的邏輯
            // 這裡需要根據您的資料庫結構來實作
            return false;
        } catch (Exception e) {
            logger.error("檢查被拒絕商家申請失敗", e);
            return false;
        }
    }

    /**
     * 檢查用戶是否有資格成為商家
     */
    public boolean isEligibleForVendor(Integer userId) {
        try {
            User user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

            // 檢查用戶是否已經是商家
            if (user.getUserRole() == User.UserRole.VENDOR) {
                return false;
            }

            // 檢查郵箱是否已驗證
            if (!user.isEmailVerified()) {
                return false;
            }

            // 檢查是否有未完成的申請
            if (hasPendingVendorApplication(userId)) {
                return false;
            }

            // 檢查是否有被拒絕的申請
            if (hasRejectedVendorApplication(userId)) {
                return false;
            }

            // TODO: 添加其他資格檢查邏輯
            // 例如：檢查用戶年齡、信用評分等

            return true;
        } catch (Exception e) {
            logger.error("檢查商家資格失敗", e);
            return false;
        }
    }

    /**
     * 將用戶轉換為商家
     */
    @Transactional
    public Map<String, Object> convertToVendor(Integer userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            User user = usersRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用戶不存在"));

            // 檢查用戶是否已經是商家
            if (user.getUserRole() == User.UserRole.VENDOR) {
                throw new RuntimeException("用戶已經是商家身份");
            }

            // 檢查用戶是否有資格成為商家
            if (!isEligibleForVendor(userId)) {
                throw new RuntimeException("用戶不符合成為商家的資格");
            }

            // 創建新的商家資料
            Vendor vendor = new Vendor();
            vendor.setId(userId); // 使用用戶ID作為商家ID
            vendor.setName(user.getEmail().split("@")[0]); // 預設使用郵箱前綴作為商家名稱
            vendor.setStatus(false); // 預設狀態為未啟用
            vendor.setRegistrationDate(new Date()); // 設置註冊時間
            vendor.setUpdatedDate(new Date()); // 設置更新時間

            // 保存商家資料
            vendorRepository.save(vendor);

            // 更新用戶角色
            user.setUserRole(User.UserRole.VENDOR);
            usersRepository.save(user);

            result.put("success", true);
            result.put("vendorId", vendor.getId());
            result.put("message", "成功轉換為商家");

            logger.info("用戶 {} 成功轉換為商家", userId);

        } catch (Exception e) {
            logger.error("轉換商家失敗", e);
            result.put("success", false);
            result.put("message", e.getMessage());
        }

        return result;
    }
}
