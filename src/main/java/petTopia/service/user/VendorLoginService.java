package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;

import petTopia.model.user.UsersBean;
import petTopia.repository.user.UsersRepository;
import petTopia.model.user.VendorBean;
import petTopia.repository.user.VendorRepository;

@Service
@Transactional
public class VendorLoginService extends BaseUserService {
    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    public Map<String, Object> registerVendor(UsersBean user, VendorBean vendor) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 檢查是否已註冊為商家
            UsersBean existingVendor = usersRepository.findByEmailAndUserRole(
                user.getEmail(), 
                UsersBean.UserRole.VENDOR
            );
            
            if (existingVendor != null) {
                result.put("success", false);
                result.put("message", "此 email 已註冊為商家");
                return result;
            }

            // 2. 加密密碼並設置角色
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user.setUserRole(UsersBean.UserRole.VENDOR);
            
            // 3. 在同一個事務中保存 user
            entityManager.persist(user);
            entityManager.flush();
            
            // 4. 設置商家資訊
            vendor.setUser(user);
            vendor.setStatus(false);
            vendor.setVendorCategoryId(1);
            vendor.setUpdatedDate(LocalDateTime.now());
            
            // 5. 在同一個事務中保存 vendor
            entityManager.persist(vendor);
            entityManager.flush();
            
            result.put("success", true);
            result.put("message", "註冊成功");
            result.put("userId", user.getId());
            
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "註冊失敗：" + e.getMessage());
        }
        
        return result;
    }

    public Map<String, Object> vendorLogin(String email, String password) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            UsersBean user = usersRepository.findByEmailAndUserRole(email, UsersBean.UserRole.VENDOR);
            
            if (user == null) {
                result.put("success", false);
                result.put("message", "商家帳號不存在");
                return result;
            }
            
            if (!passwordEncoder.matches(password, user.getPassword())) {
                result.put("success", false);
                result.put("message", "密碼錯誤");
                return result;
            }
            
            result.put("success", true);
            result.put("message", "登入成功");
            result.put("user", user);
            
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "登入失敗：" + e.getMessage());
        }
        
        return result;
    }
}
