package petTopia.controller.user;

import java.util.HashMap;
import java.util.Map;
import java.net.URLEncoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import petTopia.model.user.Users;
import petTopia.service.user.VendorLoginService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/vendor")
public class VendorLoginController {
    private static final Logger logger = LoggerFactory.getLogger(VendorLoginController.class);

    @Autowired
    private VendorLoginService vendorService;

    @GetMapping("/vendor_login")
    public String showLoginPage(Model model) {
        // 初始化 errors map
        model.addAttribute("errors", new HashMap<String, String>());
        return "vendor/vendor_login";
    }

    @GetMapping("/vendor_admin_profile")
    public String showVendorAdminProfile(HttpSession session, Model model) {
        // 檢查是否已登入
        Users loggedInUser = (Users) session.getAttribute("loggedInUser");
        if (loggedInUser == null || loggedInUser.getUserRole() != Users.UserRole.VENDOR) {
            return "redirect:/vendor/vendor_login";
        }
        
        // 將商家信息添加到模型中
        model.addAttribute("vendorName", session.getAttribute("vendorName"));
        model.addAttribute("vendorEmail", session.getAttribute("vendorEmail"));
        model.addAttribute("userId", session.getAttribute("userId"));
        
        return "vendor/vendor_admin_profile";
    }

    @PostMapping("/vendor_login")
    public String processLogin(@RequestParam(required = false) String email, 
                             @RequestParam(required = false) String password,
                             Model model,
                             HttpSession session) {
        Map<String, String> errors = new HashMap<>();
        
        logger.info("開始處理商家登入請求 - 電子郵件: {}", email);

        // **1. 基本驗證**
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "請輸入電子郵件");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "請輸入密碼");
        }

        // **2. 如果有錯誤，立即返回**
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "vendor/vendor_login";
        }

        try {
            logger.debug("開始驗證商家資訊");
            // **3. 驗證商家**
            Map<String, Object> loginResult = vendorService.vendorLogin(email, password);

            if (!(Boolean) loginResult.get("success")) {
                logger.warn("商家登入失敗 - {} - 電子郵件: {}", loginResult.get("message"), email);
                errors.put("loginFailed", (String) loginResult.get("message"));
                model.addAttribute("errors", errors);
                return "vendor/vendor_login";
            }

            // 登入成功，設置session
            session.setAttribute("loggedInUser", loginResult.get("user"));
            session.setAttribute("userId", loginResult.get("userId"));
            session.setAttribute("vendorName", loginResult.get("vendorName"));
            session.setAttribute("userRole", loginResult.get("userRole"));
            session.setAttribute("vendorEmail", email);

            logger.info("商家登入成功 - 使用者ID: {}", loginResult.get("userId"));

            // 重定向至商家管理頁面
            return "redirect:/vendor/vendor_admin_profile";

        } catch (Exception e) {
            logger.error("登入過程發生異常 - 電子郵件: {} - 錯誤訊息: {}", email, e.getMessage(), e);
            errors.put("systemError", "系統發生錯誤，請稍後再試");
            model.addAttribute("errors", errors);
            return "vendor/vendor_login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) throws Exception {
        session.invalidate();
        return "redirect:/login?logout=true&message=" + URLEncoder.encode("商家登出成功", "UTF-8");
    }
}
