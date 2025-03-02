package petTopia.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/member")
public class MemberLoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "member_login";
    }
} 