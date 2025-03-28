package petTopia.service.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import petTopia.model.user.User;
import petTopia.repository.user.UserRepository;
import petTopia.model.user.Admin;
import petTopia.repository.user.AdminRepository;
import petTopia.model.user.Member;
import petTopia.repository.user.MemberRepository;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.HashMap;

@Service
public class AdminService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AdminRepository adminRepository;
    
    @Autowired
    private MemberRepository memberRepository;
    
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
    
    // 獲取所有會員（支援分頁、搜尋和篩選）
    public Map<String, Object> getAllMembersWithFilters(
            int page, int size, String keyword, String status,
            String startDate, String endDate, String phone, String email) {
        
        // 獲取所有會員
        List<User> allMembers = userRepository.findByUserRole(User.UserRole.MEMBER);
        
        // 根據條件過濾
        List<User> filteredMembers = allMembers.stream()
            .filter(member -> {
                // 關鍵字搜尋
                if (keyword != null && !keyword.isEmpty()) {
                    String searchStr = keyword.toLowerCase();
                    return String.valueOf(member.getId()).contains(searchStr) ||
                           member.getEmail().toLowerCase().contains(searchStr);
                }
                return true;
            })
            .filter(member -> {
                // 狀態篩選
                if (status != null && !status.isEmpty()) {
                    return member.isEmailVerified() == "active".equals(status);
                }
                return true;
            })
            .filter(member -> {
                // 電子郵件搜尋
                if (email != null && !email.isEmpty()) {
                    return member.getEmail().toLowerCase().contains(email.toLowerCase());
                }
                return true;
            })
            .collect(Collectors.toList());
        
        // 獲取會員詳細資訊
        List<Map<String, Object>> memberDetails = filteredMembers.stream()
            .map(user -> {
                Member member = memberRepository.findByUserId(user.getId()).orElse(null);
                Map<String, Object> detail = new HashMap<>();
                detail.put("id", user.getId());
                detail.put("email", user.getEmail());
                detail.put("emailVerified", user.isEmailVerified());
                if (member != null) {
                    detail.put("name", member.getName());
                    detail.put("phone", member.getPhone());
                    detail.put("updatedDate", member.getUpdatedDate());
                } else {
                    detail.put("name", "");
                    detail.put("phone", "");
                    detail.put("updatedDate", null);
                }
                return detail;
            })
            .collect(Collectors.toList());
        
        // 計算分頁
        int totalElements = memberDetails.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int startIndex = page * size;
        int endIndex = Math.min(startIndex + size, totalElements);
        
        List<Map<String, Object>> pageContent = memberDetails.subList(startIndex, endIndex);
        
        return Map.of(
            "content", pageContent,
            "totalElements", totalElements,
            "totalPages", totalPages,
            "currentPage", page
        );
    }
    
    // 新增會員
    @Transactional
    public User createMember(Map<String, Object> memberData) {
        // 檢查電子郵件是否已存在
        if (userRepository.existsByEmail((String) memberData.get("email"))) {
            throw new RuntimeException("該電子郵件已被使用");
        }
        
        // 創建用戶記錄
        User user = new User();
        user.setEmail((String) memberData.get("email"));
        user.setPassword(passwordEncoder.encode((String) memberData.get("password")));
        user.setUserRole(User.UserRole.MEMBER);
        user.setEmailVerified(true);
        user.setProvider(User.Provider.LOCAL);
        user.setLocalEnabled(true);
        
        // 保存用戶記錄
        User savedUser = userRepository.save(user);
        
        // 創建會員記錄（選填欄位）
        Member member = new Member();
        member.setUser(savedUser);
        member.setStatus(true);
        member.setUpdatedDate(LocalDateTime.now());
        
        // 設置選填欄位
        if (memberData.containsKey("name")) {
            member.setName((String) memberData.get("name"));
        }
        if (memberData.containsKey("phone")) {
            member.setPhone((String) memberData.get("phone"));
        }
        if (memberData.containsKey("birthdate")) {
            member.setBirthdate(LocalDate.parse((String) memberData.get("birthdate")));
        }
        if (memberData.containsKey("gender")) {
            member.setGender((Boolean) memberData.get("gender"));
        }
        if (memberData.containsKey("address")) {
            member.setAddress((String) memberData.get("address"));
        }
        
        memberRepository.save(member);
        
        return savedUser;
    }
    
    // 刪除會員
    @Transactional
    public void deleteMember(Integer memberId) {
        User user = userRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("找不到該會員"));
            
        if (user.getUserRole() != User.UserRole.MEMBER) {
            throw new RuntimeException("該用戶不是會員");
        }
        
        // 刪除會員記錄
        memberRepository.deleteByUserId(memberId);
        // 刪除用戶記錄
        userRepository.delete(user);
    }
    
    // 批量更新會員狀態
    @Transactional
    public void batchUpdateMemberStatus(List<Integer> memberIds, String action) {
        boolean isActive = "activate".equals(action);
        
        for (Integer memberId : memberIds) {
            User user = userRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("找不到會員 ID: " + memberId));
                
            if (user.getUserRole() != User.UserRole.MEMBER) {
                throw new RuntimeException("用戶 ID: " + memberId + " 不是會員");
            }
            
            user.setEmailVerified(isActive);
        }
        
        userRepository.saveAll(userRepository.findAllById(memberIds));
    }
} 