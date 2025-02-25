package petTopia.controller.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import petTopia.model.user.UsersBean;
import petTopia.service.user.MemberLoginService;


@Controller
public class MemberLoginController {
    @Autowired
    private MemberLoginService memberService;

    @GetMapping("/member_login")
    public String showLoginPage(Model model) {
        model.addAttribute("errors", new HashMap<String, String>());
        return "member_login";
    }

    @PostMapping("/member_login/controller")
    public String processLogin(@RequestParam(required = false) String email, 
                             @RequestParam(required = false) String password,
                             Model model) {
        Map<String, String> errors = new HashMap<>();
        model.addAttribute("errors", errors);

        // 驗證 email
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "請輸入電子郵件");
        }

        // 驗證 password
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "請輸入密碼");
        }

        // 如果有錯誤，返回登入頁面
        if (!errors.isEmpty()) {
            return "member_login";
        }

        try {
            UsersBean user = memberService.memberLogin(email, password);

            if (user == null) {
                errors.put("loginFailed", "電子郵件或密碼錯誤");
                return "member_login";
            }
            
            // 確認是否為會員用戶
            if (user.getUserRole() != UsersBean.UserRole.MEMBER) {
                errors.put("loginFailed", "此帳號不是會員帳號");
                return "member_login";
            }

            return "redirect:/index_melody";

        } catch (Exception e) {
            errors.put("systemError", "系統發生錯誤，請稍後再試");
            return "member_login";
        }
    }
} 