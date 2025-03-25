package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import petTopia.jwt.JwtUtil;      
import petTopia.model.user.User;
import petTopia.model.user.Admin;
import petTopia.service.user.AdminService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.util.Date;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5174")  // 只允許後台管理員端口訪問
public class AdminController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService adminService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 初始化超級管理員帳號
     */
    @PostMapping("/init-sa")
    public ResponseEntity<?> initializeSuperAdmin() {
        try {
            adminService.initSuperAdmin("sa@pettopia.com", "test123");
            return ResponseEntity.ok(Map.of(
                "message", "超級管理員帳號已初始化",
                "email", "sa@pettopia.com",
                "password", "test123"
            ));
        } catch (Exception e) {
            logger.error("初始化超級管理員失敗", e);
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
            Map<String, Object> loginResult = adminService.adminLogin(email, password);
            
            // 從 loginResult 中獲取管理員資訊
            Admin admin = (Admin) loginResult.get("admin");
            String token = (String) loginResult.get("token");
            
            // 設置認證信息
            UserDetails userDetails = org.springframework.security.core.userdetails.User
                .withUsername(email)
                .password("")
                .roles(admin.getRole().name())
                .build();
                
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // 構建返回的資料結構
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("email", email);
            response.put("role", admin.getRole().name());
            response.put("adminId", admin.getUsers().getId());
            response.put("isAuthenticated", true);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("管理員登入失敗", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("error", "登入失敗，請確認帳號密碼"));
        }
    }
    
    /**
     * 管理員登出
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        try {
            // 獲取請求信息
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            String ipAddress = request.getRemoteAddr();
            String userAgent = request.getHeader("User-Agent");
            
            // 嘗試獲取當前認證信息
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication != null ? authentication.getName() : "未知用戶";
            
            logger.info("管理員登出 - 電子郵件: {}, IP: {}, User-Agent: {}", 
                email, 
                ipAddress,
                userAgent);
            
            // 清除認證信息
            SecurityContextHolder.clearContext();
            
            return ResponseEntity.ok(Map.of(
                "message", "登出成功",
                "email", email,
                "timestamp", new Date().getTime(),
                "ipAddress", ipAddress
            ));
        } catch (Exception e) {
            logger.error("管理員登出過程發生異常", e);
            // 即使發生異常，也確保清除認證信息
            SecurityContextHolder.clearContext();
            return ResponseEntity.ok(Map.of(
                "message", "登出成功",
                "error", "登出過程中發生異常，但已清除認證信息"
            ));
        }
    }
    
    /**
     * 獲取管理後台資料
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
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
     * 獲取當前登入管理員資訊
     */
    @GetMapping("/current-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getCurrentAdmin(@AuthenticationPrincipal UserDetails userDetails) {
        try {
            // 獲取當前認證的管理員資訊
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "未認證"));
            }

            // 從 UserDetails 中獲取資訊
            String email = userDetails.getUsername();
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
            
            // 構建返回的資料結構
            Map<String, Object> adminInfo = new HashMap<>();
            adminInfo.put("email", email);
            adminInfo.put("authorities", authorities);
            adminInfo.put("isAuthenticated", true);
            adminInfo.put("adminId", userDetails.getUsername()); // 使用 email 作為 ID
            
            return ResponseEntity.ok(adminInfo);
        } catch (Exception e) {
            logger.error("獲取當前管理員資訊失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲取資訊失敗：" + e.getMessage()));
        }
    }
} 