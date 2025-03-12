package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import petTopia.model.user.Users;
import petTopia.service.user.AdminService;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    
    // 顯示管理員登入頁面
    @GetMapping("/login")
    public String showLoginPage() {
        return "admin_login";
    }
    
    // 處理管理員登入
    @PostMapping("/login")
    public String processLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model) {
        
        Users admin = adminService.adminLogin(email, password);
        
        if (admin != null && admin.isAdmin()) {
            session.setAttribute("admin", admin);
            return "redirect:/admin/dashboard";
        }
        
        model.addAttribute("error", "登入失敗");
        return "admin_login";
    }
    
    // 顯示管理後台
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        Users admin = (Users) session.getAttribute("admin");
        if (admin == null || !admin.isAdmin()) {
            return "redirect:/admin/login";
        }
        
        model.addAttribute("members", adminService.getAllMembers());
        model.addAttribute("vendors", adminService.getAllVendors());
        return "admin_dashboard";
    }
    
    // 用戶管理API
    @PostMapping("/users/{userId}/toggle-status")
    @ResponseBody
    public Map<String, Object> toggleUserStatus(
            @PathVariable Integer userId,
            @RequestParam Boolean isActive,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        Users admin = (Users) session.getAttribute("admin");
        
        if (admin == null || !admin.isAdmin()) {
            response.put("success", false);
            response.put("message", "無權限執行此操作");
            return response;
        }
        
        try {
            adminService.toggleUserStatus(userId, isActive);
            response.put("success", true);
            response.put("message", "操作成功");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }
    
    // 登出
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("admin");
        return "redirect:/admin/login";
    }
} 