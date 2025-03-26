package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import petTopia.model.user.User;
import petTopia.repository.user.UserRepository;

import java.util.List;

@Service
public class AdminService {
    
    @Autowired
    private UserRepository usersRepository;
    
    // 管理員登入
    public User adminLogin(String email, String password) {
        User admin = usersRepository.findByEmailAndUserRole(email, User.UserRole.ADMIN);
        if (admin != null && password.equals(admin.getPassword())) {
            return admin;
        }
        return null;
    }
    
    // 獲取所有用戶列表
    public List<User> getAllUsers() {
        return usersRepository.findAll();
    }
    
    // 獲取所有會員
    public List<User> getAllMembers() {
        return usersRepository.findByUserRole(User.UserRole.MEMBER);
    }
    
    // 獲取所有商家
    public List<User> getAllVendors() {
        return usersRepository.findByUserRole(User.UserRole.VENDOR);
    }
    
    // 停用/啟用用戶
    @Transactional
    public void toggleUserStatus(Integer userId, Boolean isActive) {
        User user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
        user.setEmailVerified(isActive);  // 使用 emailVerified 作為啟用狀態
        usersRepository.save(user);
    }
    
    // 創建管理員帳號
    @Transactional
    public User createAdmin(User admin, boolean isSuperAdmin) {
        admin.setUserRole(User.UserRole.ADMIN);
        admin.setIsSuperAdmin(isSuperAdmin);
        admin.setAdminLevel(isSuperAdmin ? 1 : 0);
        return usersRepository.save(admin);
    }
} 