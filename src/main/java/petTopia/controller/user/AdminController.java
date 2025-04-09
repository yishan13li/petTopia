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
import petTopia.model.user.Member;
import petTopia.service.user.AdminService;
import petTopia.repository.user.UserRepository;
import petTopia.repository.user.AdminRepository;
import petTopia.repository.user.MemberRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Collection;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.DeleteMapping;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

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
    
    @Autowired
    private MemberRepository memberRepository;
    
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
     * 獲取所有會員（支援分頁、搜尋和篩選）
     */
    @GetMapping("/members")
    public ResponseEntity<?> getAllMembers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String email) {
        
        logger.info("獲取會員列表 - 頁碼: {}, 每頁數量: {}", page, size);
        
        try {
            Map<String, Object> response = adminService.getAllMembersWithFilters(
                page, size, keyword, status, null, null, null, email
            );
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("獲取會員列表失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲取資料失敗：" + e.getMessage()));
        }
    }
    
    /**
     * 新增會員
     */
    @PostMapping("/members")
    public ResponseEntity<?> createMember(@RequestBody Map<String, Object> memberData) {
        logger.info("新增會員");
        
        try {
            // 驗證必要欄位
            if (!memberData.containsKey("email") || !memberData.containsKey("password")) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "電子郵件和密碼為必填欄位"));
            }
            
            User newMember = adminService.createMember(memberData);
            return ResponseEntity.ok(Map.of(
                "message", "會員新增成功",
                "memberId", newMember.getId()
            ));
        } catch (Exception e) {
            logger.error("新增會員失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "新增失敗：" + e.getMessage()));
        }
    }
    
    /**
     * 刪除會員
     */
    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<?> deleteMember(@PathVariable Integer memberId) {
        logger.info("刪除會員 - ID: {}", memberId);
        
        try {
            adminService.deleteMember(memberId);
            return ResponseEntity.ok(Map.of("message", "會員刪除成功"));
        } catch (Exception e) {
            logger.error("刪除會員失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "刪除失敗：" + e.getMessage()));
        }
    }
    
    /**
     * 批量更新會員狀態
     */
    @PostMapping("/members/batch-update")
    public ResponseEntity<?> batchUpdateMembers(@RequestBody Map<String, Object> updateData) {
        logger.info("批量更新會員狀態");
        
        try {
            List<Integer> memberIds = (List<Integer>) updateData.get("memberIds");
            String action = (String) updateData.get("action");
            
            adminService.batchUpdateMemberStatus(memberIds, action);
            return ResponseEntity.ok(Map.of("message", "會員狀態更新成功"));
        } catch (Exception e) {
            logger.error("批量更新會員狀態失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "更新失敗：" + e.getMessage()));
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
    
    @GetMapping("/members/{memberId}")
    public ResponseEntity<?> getMember(@PathVariable Integer memberId) {
        logger.info("獲取會員資料 - ID: {}", memberId);
        
        try {
            User user = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("找不到該會員"));
                
            if (user.getUserRole() != User.UserRole.MEMBER) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "該用戶不是會員"));
            }
            
            Member member = memberRepository.findByUserId(memberId)
                .orElseThrow(() -> new RuntimeException("找不到會員資料"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", user.getId());
            response.put("email", user.getEmail());
            response.put("emailVerified", user.isEmailVerified());
            response.put("name", member.getName());
            response.put("phone", member.getPhone());
            response.put("birthdate", member.getBirthdate());
            response.put("address", member.getAddress());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("獲取會員資料失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲取資料失敗：" + e.getMessage()));
        }
    }
    
    @PutMapping("/members/{memberId}")
    public ResponseEntity<?> updateMember(@PathVariable Integer memberId, @RequestBody Map<String, Object> request) {
        try {
            User user = userRepository.findById(memberId)
                    .orElseThrow(() -> new RuntimeException("找不到該會員"));
            
            if (user.getUserRole() != User.UserRole.MEMBER) {
                return ResponseEntity.badRequest().body("該用戶不是會員");
            }

            // 更新會員資料
            Member member = memberRepository.findByUserId(memberId)
                    .orElseThrow(() -> new RuntimeException("找不到該會員資料"));

            // 更新會員基本資料
            if (request.containsKey("name")) {
                member.setName((String) request.get("name"));
            }
            if (request.containsKey("phone")) {
                member.setPhone((String) request.get("phone"));
            }
            if (request.containsKey("address")) {
                member.setAddress((String) request.get("address"));
            }
            
            // 處理生日日期
            if (request.containsKey("birthdate")) {
                String birthdateStr = (String) request.get("birthdate");
                if (birthdateStr != null && !birthdateStr.trim().isEmpty()) {
                    try {
                        LocalDate birthdate = LocalDate.parse(birthdateStr);
                        member.setBirthdate(birthdate);
                    } catch (DateTimeParseException e) {
                        return ResponseEntity.badRequest().body("生日日期格式不正確");
                    }
                }
            }

            // 更新用戶狀態
            if (request.containsKey("emailVerified")) {
                user.setEmailVerified((Boolean) request.get("emailVerified"));
            }

            // 保存更新
            memberRepository.save(member);
            userRepository.save(user);

            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("更新會員資料失敗", e);
            return ResponseEntity.badRequest().body("更新會員資料失敗: " + e.getMessage());
        }
    }
} 