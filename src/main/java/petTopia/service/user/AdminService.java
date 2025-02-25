package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import petTopia.model.user.UsersBean;
import petTopia.repository.user.UsersRepository;

import java.util.List;

@Service
public class AdminService {
    
    @Autowired
    private UsersRepository usersRepository;
    
    // 管理員登入
    public UsersBean adminLogin(String email, String password) {
        UsersBean admin = usersRepository.findByEmailAndUserRole(email, UsersBean.UserRole.ADMIN);
        if (admin != null && password.equals(admin.getPassword())) {
            return admin;
        }
        return null;
    }
    
    // 獲取所有用戶列表
    public List<UsersBean> getAllUsers() {
        return usersRepository.findAll();
    }
    
    // 獲取所有會員
    public List<UsersBean> getAllMembers() {
        return usersRepository.findByUserRole(UsersBean.UserRole.MEMBER);
    }
    
    // 獲取所有商家
    public List<UsersBean> getAllVendors() {
        return usersRepository.findByUserRole(UsersBean.UserRole.VENDOR);
    }
    
    // 停用/啟用用戶
    @Transactional
    public void toggleUserStatus(Integer userId, Boolean isActive) {
        UsersBean user = usersRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("用戶不存在"));
        user.setEmailVerified(isActive);  // 使用 emailVerified 作為啟用狀態
        usersRepository.save(user);
    }
    
    // 創建管理員帳號
    @Transactional
    public UsersBean createAdmin(UsersBean admin, boolean isSuperAdmin) {
        admin.setUserRole(UsersBean.UserRole.ADMIN);
        admin.setIsSuperAdmin(isSuperAdmin);
        admin.setAdminLevel(isSuperAdmin ? 1 : 0);
        return usersRepository.save(admin);
    }
} 