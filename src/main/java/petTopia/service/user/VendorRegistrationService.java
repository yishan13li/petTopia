package petTopia.service.user;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import petTopia.model.user.User;
import petTopia.model.vendor.Vendor;
import petTopia.model.user.Member;
import petTopia.repository.user.UserRepository;
import petTopia.repository.vendor.VendorRepository;
import petTopia.repository.user.MemberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import petTopia.model.vendor.VendorCategory;
import petTopia.repository.vendor.VendorCategoryRepository;

@Service
public class VendorRegistrationService {
    
    private static final Logger logger = LoggerFactory.getLogger(VendorRegistrationService.class);
    
    @Autowired
    private UserRepository usersRepository;
    
    @Autowired
    private VendorRepository vendorRepository;
    
    @Autowired
    private VendorCategoryRepository vendorCategoryRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    @Transactional
    public Map<String, Object> register(User user) {
        Map<String, Object> result = new HashMap<>();
        logger.info("開始商家註冊流程，email: {}", user.getEmail());
        
        try {
            // 檢查是否已存在相同email的商家帳號
            User existingVendor = usersRepository.findByEmailAndUserRole(
                    user.getEmail(), User.UserRole.VENDOR);
            if (existingVendor != null) {
                logger.warn("註冊失敗：商家帳號已存在，email: {}", user.getEmail());
                result.put("success", false);
                result.put("message", "此 email 已註冊為商家");
                return result;
            }

            // 生成驗證令牌
            String token = UUID.randomUUID().toString();
            user.setVerificationToken(token);
            user.setTokenExpiry(LocalDateTime.now().plusHours(24));
            user.setUserRole(User.UserRole.VENDOR);
            user.setProvider(User.Provider.LOCAL);
            
            // 加密密碼
            String encodedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(encodedPassword);
            
            // 保存用戶
            User savedUser = usersRepository.save(user);
            logger.info("商家用戶資訊儲存成功，userId: {}", savedUser.getId());
            
            // 創建商家資料
            Vendor vendor = new Vendor();
            vendor.setUser(savedUser);  // 設置關聯，ID 會自動對應
            vendor.setRegistrationDate(new Date());
            vendor.setUpdatedDate(new Date());
            vendor.setStatus(false); // 預設為未認證狀態
            vendor.setVendorCategory(vendorCategoryRepository.findById(1).orElse(null)); // 預設分類
            
            // 保存商家資料
            Vendor savedVendor = vendorRepository.save(vendor);
            logger.info("商家詳細資訊儲存成功，vendorId: {}", savedVendor.getId());

            // 發送驗證郵件
            emailService.sendVerificationEmail(user.getEmail(), token);
            logger.info("驗證郵件發送成功，email: {}", user.getEmail());

            result.put("success", true);
            result.put("message", "註冊成功，請查收驗證郵件");
            result.put("userId", savedUser.getId());
            result.put("vendorId", savedVendor.getId());

        } catch (Exception e) {
            logger.error("商家註冊過程發生錯誤", e);
            result.put("success", false);
            result.put("message", "註冊失敗：" + e.getMessage());
            throw new RuntimeException("商家註冊失敗", e);
        }

        return result;
    }
    
    @Transactional
    public boolean verifyEmail(String token) {
        User user = usersRepository.findByVerificationToken(token);
        
        if (user != null && !user.isEmailVerified() && 
            LocalDateTime.now().isBefore(user.getTokenExpiry())) {
            
            try {
                // 更新用戶驗證狀態
                user.setEmailVerified(true);
                user.setVerificationToken(null);
                user.setTokenExpiry(null);
                usersRepository.save(user);
                
                // 更新商家狀態
                Vendor vendor = vendorRepository.findByUserId(user.getId()).orElse(null);
                if (vendor != null) {
                    vendor.setStatus(true);
                    vendorRepository.save(vendor);
                    logger.info("商家驗證完成，userId: {}", user.getId());
                    return true;
                } else {
                    logger.error("商家驗證失敗：找不到商家資料，userId: {}", user.getId());
                }
            } catch (Exception e) {
                logger.error("商家驗證過程發生錯誤", e);
                throw new RuntimeException("商家驗證失敗", e);
            }
        } else {
            logger.warn("商家驗證失敗：驗證碼無效或已過期");
        }
        return false;
    }

    public User findByEmail(String email) {
        return usersRepository.findByEmailAndUserRole(email, User.UserRole.VENDOR);
    }
    
    public User findVendorByEmail(String email) {
        return usersRepository.findByEmailAndUserRole(email, User.UserRole.VENDOR);
    }

    public User findMemberByEmail(String email) {
        return usersRepository.findByEmailAndUserRole(email, User.UserRole.MEMBER);
    }

    @Transactional
    public Map<String, Object> convertMemberToVendor(Integer memberId) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        
        try {
            // 檢查會員是否存在
            User memberUser = usersRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("會員不存在"));
            
            if (memberUser.getUserRole() != User.UserRole.MEMBER) {
                throw new RuntimeException("只有會員帳號可以轉換為商家");
            }
            
            // 檢查是否已經有相同 email 的商家帳號
            User existingVendor = usersRepository.findByEmailAndUserRole(
                memberUser.getEmail(), User.UserRole.VENDOR);
            if (existingVendor != null) {
                throw new RuntimeException("此 email 已註冊為商家");
            }
            
            Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("會員資料不存在"));
            
            // 創建新的商家用戶
            User vendorUser = new User();
            vendorUser.setEmail(memberUser.getEmail());
            vendorUser.setPassword(memberUser.getPassword()); // 直接使用已加密的密碼
            vendorUser.setUserRole(User.UserRole.VENDOR);
            vendorUser.setProvider(memberUser.getProvider());
            vendorUser.setEmailVerified(true); // 因為會員已驗證過 email
            
            // 先保存商家用戶
            vendorUser = usersRepository.save(vendorUser);
            
            // 創建新的商家資料
            Vendor vendor = new Vendor();
            vendor.setUser(vendorUser);  // 設置用戶關聯
            vendor.setName(member.getName());
            vendor.setPhone(member.getPhone());
            vendor.setAddress(member.getAddress());
            vendor.setStatus(false);  // 預設未認證
            vendor.setRegistrationDate(new Date());
            vendor.setUpdatedDate(new Date());
            vendor.setVendorCategory(vendorCategoryRepository.findById(1).orElse(null)); // 預設分類
            
            // 保存商家資料
            Vendor savedVendor = vendorRepository.save(vendor);
            
            // 重新獲取完整的商家用戶資訊
            User newVendorUser = usersRepository.findById(vendorUser.getId())
                .orElseThrow(() -> new RuntimeException("商家用戶資料不存在"));
            
            result.put("success", true);
            result.put("message", "商家帳號創建成功");
            result.put("vendorUser", newVendorUser);
            result.put("vendorId", savedVendor.getId());
            result.put("vendorEmail", newVendorUser.getEmail());
            result.put("rawPassword", memberUser.getPassword()); // 返回已加密的密碼供認證使用
            
        } catch (Exception e) {
            logger.error("商家轉換過程發生錯誤", e);
            result.put("message", e.getMessage());
            throw new RuntimeException("商家轉換失敗", e);
        }
        
        return result;
    }
} 