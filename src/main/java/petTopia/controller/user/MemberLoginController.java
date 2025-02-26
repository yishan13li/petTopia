package petTopia.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import jakarta.servlet.http.HttpSession;

import petTopia.model.user.UsersBean;
import petTopia.service.user.MemberLoginService;

@Controller
public class MemberLoginController {
    @Autowired
    private MemberLoginService memberService;

    @GetMapping("/member_login")
    public String showLoginPage() {
        return "member_login";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "member_login";
    }

    @PostMapping("/member/login")
    public String processLogin(@RequestParam(required = false) String email, 
                             @RequestParam(required = false) String password,
                             Model model,
                             HttpSession session) {
        Map<String, String> errors = new HashMap<>();
        model.addAttribute("errors", errors);

        System.out.println("開始處理登入請求");
        System.out.println("Email: " + email);

        try {
            // 檢查輸入參數
            if (email == null || email.trim().isEmpty() || 
                password == null || password.trim().isEmpty()) {
                System.out.println("輸入參數為空");
                errors.put("loginFailed", "請輸入電子郵件和密碼");
                model.addAttribute("showLoginFailedModal", true);
                return "member_login";
            }

            UsersBean user = memberService.memberLogin(email, password);
            System.out.println("驗證結果: " + (user != null ? "成功" : "失敗"));

            if (user == null) {
                System.out.println("用戶驗證失敗");
                errors.put("loginFailed", "電子郵件或密碼錯誤");
                model.addAttribute("showLoginFailedModal", true);
                return "member_login";
            }
            
            System.out.println("用戶角色: " + user.getUserRole());
            if (user.getUserRole() != UsersBean.UserRole.MEMBER) {
                System.out.println("非會員帳號");
                errors.put("loginFailed", "此帳號不是會員帳號");
                model.addAttribute("showLoginFailedModal", true);
                return "member_login";
            }

            // 登入成功
            System.out.println("登入成功，設置 session");
            session.setAttribute("loginUser", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            
            return "redirect:/index";

        } catch (Exception e) {
            // 詳細記錄異常信息
            System.out.println("發生異常: " + e.getClass().getName());
            System.out.println("異常信息: " + e.getMessage());
            e.printStackTrace();
            
            // 檢查是否為特定類型的異常
            if (e instanceof org.springframework.security.authentication.BadCredentialsException) {
                errors.put("loginFailed", "帳號或密碼錯誤");
            } else {
                errors.put("loginFailed", "系統發生錯誤，請稍後再試");
            }
            
            model.addAttribute("showLoginFailedModal", true);
            return "member_login";
        }
    }

    @PostMapping("/member_login")
    public String login(HttpSession session, @RequestParam String account, @RequestParam String password) {
        UsersBean user = memberService.memberLogin(account, password);
        if (user != null) {
            // 登入成功，將用戶信息存入 session
            session.setAttribute("loginUser", user);
            return "redirect:/index";
        } else {
            return "redirect:/member_login?error";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        // 清除 session 中的用戶信息
        session.removeAttribute("loginUser");
        return "redirect:/index";
    }
} 