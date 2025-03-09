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

    @PostMapping("/login")
    public String processLogin(@RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            Model model,
            HttpSession session) {
        Map<String, String> errors = new HashMap<>();

        logger.info("開始處理登入請求 - 電子郵件: {}", email);

        // 基本驗證
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "請輸入電子郵件");
        }
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "請輸入密碼");
        }

        if (!errors.isEmpty()) {
            model.addAttribute("errors", errors);
            return "login";
        }

        try {
            Map<String, Object> loginResult = memberService.memberLogin(email, password);

            if (!(Boolean) loginResult.get("success")) {
                errors.put("loginFailed", (String) loginResult.get("message"));
                model.addAttribute("errors", errors);
                return "login";
            }

            // 登入成功，設置session
            session.setAttribute("loggedInUser", loginResult.get("user"));
            session.setAttribute("userId", loginResult.get("userId"));
            session.setAttribute("memberName", loginResult.get("memberName"));
            session.setAttribute("userRole", loginResult.get("userRole"));

            return "redirect:/index";

        } catch (Exception e) {
            logger.error("登入過程發生異常 - 電子郵件: {} - 錯誤訊息: {}", email, e.getMessage(), e);
            errors.put("systemError", "系統發生錯誤，請稍後再試");
            model.addAttribute("errors", errors);
            return "login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login?logout=true";
    }
}
