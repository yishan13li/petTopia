package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import petTopia.model.user.User;
import petTopia.repository.user.UserRepository;
import petTopia.model.user.Admin;
import petTopia.repository.user.AdminRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // 管理員登入
    public User adminLogin(String email, String password) {
        // 查找管理員帳號
        User admin = userRepository.findByEmailAndUserRole(email, User.UserRole.ADMIN);
            
        // 驗證密碼
        if (admin != null && passwordEncoder.matches(password, admin.getPassword())) {
            return admin;
        }
        
        return null;
    }
    
    // 獲取所有用戶列表
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // 獲取所有會員
    public List<User> getAllMembers() {
        return userRepository.findByUserRole(User.UserRole.MEMBER);
    }
    
    // 獲取所有商家
    public List<User> getAllVendors() {
        return userRepository.findByUserRole(User.UserRole.VENDOR);
    }
    
    // 停用/啟用用戶
    @Transactional
    public void toggleUserStatus(Integer userId, Boolean isActive) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
        user.setEmailVerified(isActive);  // 使用 emailVerified 作為啟用狀態
        userRepository.save(user);
    }
    
    // 創建管理員帳號
    @Transactional
    public User createAdmin(User admin, boolean isSuperAdmin) {
        // 設置用戶角色和權限
        admin.setUserRole(User.UserRole.ADMIN);
        admin.setIsSuperAdmin(isSuperAdmin);
        admin.setAdminLevel(isSuperAdmin ? 1 : 0);
        
        // 保存用戶記錄
        User savedUser = userRepository.save(admin);
        
        // 創建並保存管理員記錄
        Admin adminRecord = new Admin();
        adminRecord.setUsers(savedUser);
        adminRecord.setName(isSuperAdmin ? "Super Admin" : "Admin");
        adminRecord.setRole(isSuperAdmin ? Admin.AdminRole.SA : Admin.AdminRole.ADMIN);
        adminRecord.setRegistrationDate(LocalDateTime.now());
        
        adminRepository.save(adminRecord);
        
        return savedUser;
    }
} 