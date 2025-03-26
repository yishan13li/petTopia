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

import petTopia.jwt.JwtUtil;      
import petTopia.model.user.User;
import petTopia.service.user.AdminService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;

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
            // 先驗證用戶身份
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, password)
            );
            
            // 獲取用戶詳情
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            
            // 檢查是否為管理員
            if (!userDetails.getAuthorities().stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "無權限訪問管理後台"));
            }
            
            // 生成 JWT
            String token = jwtUtil.generateToken(email, 
                Integer.parseInt(userDetails.getUsername()), 
                "ADMIN");
            
            logger.info("管理員登入成功 - ID: {}", userDetails.getUsername());
            return ResponseEntity.ok(Map.of(
                "message", "登入成功",
                "token", token,
                "adminId", userDetails.getUsername(),
                "email", email,
                "role", "ADMIN"
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
} 