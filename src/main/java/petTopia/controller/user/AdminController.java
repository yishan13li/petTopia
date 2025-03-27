package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import petTopia.jwt.JwtUtil;      
import petTopia.model.user.User;
import petTopia.model.user.Admin;
import petTopia.service.user.AdminService;
import petTopia.repository.user.UserRepository;
import petTopia.repository.user.AdminRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.time.LocalDateTime;
import java.util.Collections;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    /**
     * 初始化超級管理員帳號
     */
    @PostMapping("/init-sa")
    @PreAuthorize("permitAll()")
    public ResponseEntity<?> initSuperAdmin() {
        logger.info("初始化超級管理員帳號");
        
        try {
            // 檢查是否已存在超級管理員
            User existingAdmin = userRepository.findByEmailAndUserRole("sa@pettopia.com", User.UserRole.ADMIN);
            if (existingAdmin != null && existingAdmin.getIsSuperAdmin()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "超級管理員帳號已存在"));
            }
            
            // 創建超級管理員帳號
            User superAdmin = new User();
            superAdmin.setEmail("sa@pettopia.com");
            superAdmin.setPassword(passwordEncoder.encode("test123"));
            superAdmin.setUserRole(User.UserRole.ADMIN);
            superAdmin.setEmailVerified(true);
            superAdmin.setIsSuperAdmin(true);
            superAdmin.setAdminLevel(1);
            superAdmin.setProvider("LOCAL");
            superAdmin.setLocalEnabled(true);
            
            // 創建並關聯 Admin 記錄
            Admin admin = new Admin();
            admin.setName("Super Admin");
            admin.setRole(Admin.AdminRole.SA);
            admin.setUsers(superAdmin);
            admin.setRegistrationDate(LocalDateTime.now());
            
            // 保存超級管理員帳號和關聯的 Admin 記錄
            adminService.createAdmin(superAdmin, true);
            
            logger.info("超級管理員帳號初始化成功");
            return ResponseEntity.ok(Map.of(
                "message", "超級管理員帳號初始化成功",
                "email", "sa@pettopia.com"
            ));
        } catch (Exception e) {
            logger.error("超級管理員帳號初始化失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "初始化失敗：" + e.getMessage()));
        }
    }
    
    /**
     * 管理員登入
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String email = credentials.get("email");
        String password = credentials.get("password");
        
        logger.info("處理管理員登入請求 - 電子郵件: {}", email);
        
        if (email == null || password == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "電子郵件和密碼不能為空"));
        }
        
        try {
            // 使用 adminService 進行認證
            User admin = adminService.adminLogin(email, password);
            
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "登入失敗，請確認帳號密碼"));
            }
            
            // 生成 JWT
            String token = jwtUtil.generateToken(email, admin.getId(), "ADMIN");
            
            logger.info("管理員登入成功 - ID: {}", admin.getId());
            return ResponseEntity.ok(Map.of(
                "message", "登入成功",
                "token", token,
                "adminId", admin.getId(),
                "email", email,
                "role", "ADMIN",
                "isAuthenticated", true
            ));
        } catch (Exception e) {
            logger.error("管理員登入過程發生異常", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "登入失敗，請確認帳號密碼"));
        }
    }
    
    /**
     * 管理員登出
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        logger.info("處理管理員登出請求");
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "登出成功"));
    }
    
    /**
     * 獲取管理後台資料
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardData() {
        logger.info("獲取管理後台資料");
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            Map<String, Object> dashboardData = new HashMap<>();
            dashboardData.put("members", adminService.getAllMembers());
            dashboardData.put("vendors", adminService.getAllVendors());
            dashboardData.put("adminInfo", Map.of(
                "id", userDetails.getUsername(),
                "email", userDetails.getUsername(),
                "role", "ADMIN"
            ));
            
            return ResponseEntity.ok(dashboardData);
        } catch (Exception e) {
            logger.error("獲取管理後台資料失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲取資料失敗：" + e.getMessage()));
        }
    }
    
    /**
     * 切換用戶狀態（啟用/停用）
     */
    @PutMapping("/users/{userId}/status")
    public ResponseEntity<?> toggleUserStatus(
            @PathVariable Integer userId,
            @RequestBody Map<String, Boolean> statusUpdate) {
        
        Boolean isActive = statusUpdate.get("isActive");
        if (isActive == null) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "狀態參數不能為空"));
        }
        
        logger.info("切換用戶狀態 - 用戶ID: {}, 狀態: {}", userId, isActive);
        
        try {
            adminService.toggleUserStatus(userId, isActive);
            
            logger.info("用戶狀態切換成功 - 用戶ID: {}, 狀態: {}", userId, isActive);
            return ResponseEntity.ok(Map.of(
                "message", "用戶狀態已更新",
                "userId", userId,
                "isActive", isActive
            ));
        } catch (Exception e) {
            logger.error("用戶狀態切換失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "操作失敗：" + e.getMessage()));
        }
    }
    
    /**
     * 獲取所有會員
     */
    @GetMapping("/members")
    public ResponseEntity<?> getAllMembers() {
        logger.info("獲取所有會員資料");
        
        try {
            return ResponseEntity.ok(Map.of("members", adminService.getAllMembers()));
        } catch (Exception e) {
            logger.error("獲取會員資料失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲取資料失敗：" + e.getMessage()));
        }
    }
    
    /**
     * 獲取所有商家
     */
//    @GetMapping("/vendors")
//    public ResponseEntity<?> getAllVendors() {
//        logger.info("獲取所有商家資料");
//        
//        try {
//            return ResponseEntity.ok(Map.of("vendors", adminService.getAllVendors()));
//        } catch (Exception e) {
//            logger.error("獲取商家資料失敗", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(Map.of("error", "獲取資料失敗：" + e.getMessage()));
//        }
//    }
    
    /**
     * 檢查管理員登入狀態
     */
    @GetMapping("/status")
    public ResponseEntity<?> checkLoginStatus() {
        logger.info("檢查管理員登入狀態");
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated() && 
                !"anonymousUser".equals(authentication.getPrincipal())) {
                
                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
                
                // 檢查是否為管理員
                if (!userDetails.getAuthorities().stream()
                        .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                    return ResponseEntity.ok(Map.of("isLoggedIn", false));
                }
                
                return ResponseEntity.ok(Map.of(
                    "isLoggedIn", true,
                    "adminId", userDetails.getUsername(),
                    "email", userDetails.getUsername(),
                    "role", "ADMIN"
                ));
            }
            
            return ResponseEntity.ok(Map.of("isLoggedIn", false));
        } catch (Exception e) {
            logger.error("檢查管理員登入狀態失敗", e);
            return ResponseEntity.ok(Map.of("isLoggedIn", false));
        }
    }
    
    /**
     * 獲取當前管理員資訊
     */
    @GetMapping("/current-admin")
    public ResponseEntity<?> getCurrentAdmin() {
        logger.info("獲取當前管理員資訊");
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated() || 
                "anonymousUser".equals(authentication.getPrincipal())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未登入"));
            }
            
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String email = userDetails.getUsername();
            
            // 獲取管理員資訊
            User admin = userRepository.findByEmailAndUserRole(email, User.UserRole.ADMIN);
            if (admin == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "找不到管理員資訊"));
            }
            
            // 獲取關聯的 Admin 記錄
            Admin adminRecord = adminRepository.findById(admin.getId()).orElse(null);
            
            return ResponseEntity.ok(Map.of(
                "email", admin.getEmail(),
                "adminId", admin.getId(),
                "role", "ADMIN",
                "isAuthenticated", true,
                "authorities", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")),
                "adminInfo", adminRecord != null ? Map.of(
                    "name", adminRecord.getName(),
                    "role", adminRecord.getRole(),
                    "registrationDate", adminRecord.getRegistrationDate()
                ) : null
            ));
        } catch (Exception e) {
            logger.error("獲取當前管理員資訊失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲取資訊失敗：" + e.getMessage()));
        }
    }
} 