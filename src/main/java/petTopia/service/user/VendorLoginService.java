package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import petTopia.model.user.UsersBean;
import petTopia.repository.user.UsersRepository;
import petTopia.model.user.VendorBean;
import petTopia.repository.user.VendorRepository;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

@Service
@Transactional
public class VendorLoginService extends BaseUserService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private PasswordEncoder springPasswordEncoder;

    public Map<String, Object> registerVendor(String email, String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 創建基本用戶並獲取 user_id
            UsersBean user = createBaseUser(email, password, UsersBean.UserRole.VENDOR);
            
            // 創建商家資料
            VendorBean vendor = new VendorBean();
            vendor.setId(user.getId());  // 使用相同的 ID
            vendor.setStatus(false);     // 預設未驗證

            
            // 保存商家資料
            VendorBean savedVendor = vendorRepository.save(vendor);
            
            // 發送驗證郵件
            emailService.sendVerificationEmail(email, user.getVerificationToken());
            
            result.put("success", true);
            result.put("userId", user.getId());
            result.put("vendorId", savedVendor.getId());
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("error", e.getMessage());
        }
        
        return result;
    }
    
    public UsersBean vendorLogin(String email, String password) {
        UsersBean user = findByEmail(email);
        if (user == null || user.getUserRole() != UsersBean.UserRole.VENDOR) {
            return null;
        }
        return passwordEncoder.matches(password, user.getPassword()) ? user : null;
    }

    public Map<String, Object> registerVendor(UsersBean user, VendorBean vendor) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 檢查 email 是否已存在
            UsersBean existingUser = usersRepository.findByEmail(user.getEmail());
            if (existingUser != null) {
                result.put("success", false);
                result.put("message", "此 email 已被註冊");
                return result;
            }

            // 2. 加密密碼
            user.setPassword(springPasswordEncoder.encode(user.getPassword()));
            
            // 3. 保存用戶信息
            UsersBean savedUser = usersRepository.save(user);
            
            // 4. 設置商家關聯
            vendor.setId(savedUser.getId());
            vendor.setStatus(false);  // 使用 boolean
            
            // 5. 保存商家信息
            vendorRepository.save(vendor);
            
            result.put("success", true);
            result.put("message", "註冊成功");
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "註冊失敗：" + e.getMessage());
        }
        
        return result;
    }
}
