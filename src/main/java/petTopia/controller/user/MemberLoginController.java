package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpSession;
import petTopia.model.user.Users;
import petTopia.service.user.MemberLoginService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

@Controller
public class MemberLoginController {
    private static final Logger logger = LoggerFactory.getLogger(MemberLoginController.class);

    @Autowired
    private MemberLoginService memberService;

    @GetMapping("/login")
    public String showLoginPage(Model model, @RequestParam(required = false) String registered) {
        Map<String, String> messages = new HashMap<>();
        if (registered != null && registered.equals("true")) {
            messages.put("success", "註冊成功，請登入");
        }
        model.addAttribute("messages", messages);
        model.addAttribute("errors", new HashMap<String, String>());
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) throws UnsupportedEncodingException {
        // 記錄登出資訊
        Users user = (Users) session.getAttribute("loggedInUser");
        if (user != null) {
            logger.info("會員登出 - userId: {}, email: {}", user.getId(), user.getEmail());
        }
        
        // 清除 session
        session.invalidate();
        
        // 重定向到登入頁面，並顯示登出成功訊息
        return "redirect:/login?logout=true&message=" + URLEncoder.encode("登出成功", "UTF-8");
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            Model model,
            HttpSession session) {
        Map<String, String> errors = new HashMap<>();

        logger.info("開始處理會員登入請求 - 電子郵件: {}", email);

        // 1. 基本驗證
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "請輸入電子郵件");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "請輸入密碼");
        }

        // 2. 如果有錯誤，立即返回
        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "login";
        }

        try {
            logger.debug("開始驗證會員資訊");
            // 3. 驗證使用者
            Map<String, Object> loginResult = memberService.memberLogin(email, password);

            if (!(Boolean) loginResult.get("success")) {
                logger.warn("登入失敗 - {} - 電子郵件: {}", loginResult.get("message"), email);
                errors.put("loginFailed", (String) loginResult.get("message"));
                model.addAttribute("errors", errors);
                return "login";
            }

            Users user = (Users) loginResult.get("user");
            logger.info("會員登入成功 - 使用者ID: {}", user.getId());
            
            // 設置所有必要的 session 屬性
            session.setAttribute("loggedInUser", user);
            session.setAttribute("userId", loginResult.get("userId"));
            // 如果沒有名字，使用郵箱作為顯示名稱
            String displayName = (String) loginResult.get("memberName");
            if (displayName == null || displayName.trim().isEmpty()) {
                displayName = (String) loginResult.get("email");
                logger.info("使用者沒有名字，使用郵箱作為顯示名稱: {}", displayName);
            }
            session.setAttribute("memberName", displayName);
            session.setAttribute("userRole", loginResult.get("userRole"));
            session.setAttribute("email", loginResult.get("email"));
            session.setAttribute("provider", user.getProvider());

            // 重定向至首頁
            return "redirect:/index";

        } catch (Exception e) {
            logger.error("會員登入過程發生異常 - 電子郵件: {} - 錯誤訊息: {}", email, e.getMessage(), e);
            errors.put("systemError", "系統發生錯誤，請稍後再試");
            model.addAttribute("errors", errors);
            return "login";
        }
    }
}
