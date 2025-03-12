package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import petTopia.model.user.Users;
import petTopia.repository.user.UsersRepository;

import java.util.List;

@Service
public class AdminService {
    
    @Autowired
    private UsersRepository usersRepository;
    
    // 管理員登入
    public Users adminLogin(String email, String password) {
        Users admin = usersRepository.findByEmailAndUserRole(email, Users.UserRole.ADMIN);
        if (admin != null && password.equals(admin.getPassword())) {
            return admin;
        }
        return null;
    }
    
    // 獲取所有用戶列表
    public List<Users> getAllUsers() {
        return usersRepository.findAll();
    }
    
    // 獲取所有會員
    public List<Users> getAllMembers() {
        return usersRepository.findByUserRole(Users.UserRole.MEMBER);
    }
    
    // 獲取所有商家
    public List<Users> getAllVendors() {
        return usersRepository.findByUserRole(Users.UserRole.VENDOR);
    }
    
    // 停用/啟用用戶
    @Transactional
    public void toggleUserStatus(Integer userId, Boolean isActive) {
        Users user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
        user.setEmailVerified(isActive);  // 使用 emailVerified 作為啟用狀態
        usersRepository.save(user);
    }
    
    // 創建管理員帳號
    @Transactional
    public Users createAdmin(Users admin, boolean isSuperAdmin) {
        admin.setUserRole(Users.UserRole.ADMIN);
        admin.setIsSuperAdmin(isSuperAdmin);
        admin.setAdminLevel(isSuperAdmin ? 1 : 0);
        return usersRepository.save(admin);
    }
} 