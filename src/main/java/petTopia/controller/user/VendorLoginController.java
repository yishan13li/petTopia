package petTopia.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpSession;
import petTopia.model.user.UsersBean;
import petTopia.service.user.VendorLoginService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/vendor")
public class VendorLoginController {
    private static final Logger logger = LoggerFactory.getLogger(VendorLoginController.class);

    @Autowired
    private VendorLoginService vendorService;

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        // 初始化 errors map
        model.addAttribute("errors", new HashMap<String, String>());
        return "vendor/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam(required = false) String email, 
                             @RequestParam(required = false) String password,
                             Model model,
                             HttpSession session) {
        Map<String, String> errors = new HashMap<>();
        
        logger.info("開始處理登入請求 - 電子郵件: {}", email);  // 新增日誌

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
            return "vendor/login";
        }

        try {
            logger.debug("開始驗證使用者資訊");  // 新增日誌
            // **3. 驗證使用者**
            Map<String, Object> loginResult = vendorService.vendorLogin(email, password);

            if (!(Boolean) loginResult.get("success")) {
                logger.warn("登入失敗 - {} - 電子郵件: {}", loginResult.get("message"), email);
                errors.put("loginFailed", (String) loginResult.get("message"));
                model.addAttribute("errors", errors);
                return "vendor/login";
            }

            UsersBean user = (UsersBean) loginResult.get("user");
            logger.info("商家登入成功 - 使用者ID: {}", user.getId());
            session.setAttribute("loggedInUser", user);

            // **6. 重定向至商家首頁**
            return "redirect:/index";

        } catch (Exception e) {
            logger.error("登入過程發生異常 - 電子郵件: {} - 錯誤訊息: {}", email, e.getMessage(), e);  // 改進錯誤日誌
            errors.put("systemError", "系統發生錯誤，請稍後再試");
            model.addAttribute("errors", errors);
            return "vendor/login";
        }
    }
}
