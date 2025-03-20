package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import petTopia.jwt.JwtUtil;
import petTopia.model.user.User;
import petTopia.service.user.MemberLoginService;
import jakarta.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/oauth2")
public class OAuth2RedirectController {

    @Autowired
    private MemberLoginService memberLoginService;

    @Autowired
    private JwtUtil jwtUtil;

    // 前端應用的URL
    private static final String FRONTEND_URL = "http://localhost:5173";

    @GetMapping("/callback")
    public RedirectView oauth2Callback(
            @RequestParam String email, 
            @RequestParam String name,
            @RequestParam String provider,
            HttpServletRequest request) {
        
        try {
            // 查找或創建用戶
            User user = memberLoginService.findByEmail(email);
            boolean isNewUser = false;
            
            if (user == null) {
                // 這裡應該實現實際創建用戶的邏輯
                // 這是模擬創建的示例
                isNewUser = true;
            }

            // 生成JWT令牌
            String token = jwtUtil.generateToken(
                email,
                user.getId(),
                user.getUserRole().toString()
            );

            // 構建重定向URL
            StringBuilder redirectUrl = new StringBuilder(FRONTEND_URL);
            redirectUrl.append("/login?oauth2Success=true");
            redirectUrl.append("&token=").append(token);
            redirectUrl.append("&userId=").append(user.getId());
            redirectUrl.append("&email=").append(email);
            redirectUrl.append("&role=").append(user.getUserRole().toString());
            
            if (isNewUser) {
                redirectUrl.append("&newUser=true");
            }

            return new RedirectView(redirectUrl.toString());
        } catch (Exception e) {
            // 處理錯誤情況，重定向到帶有錯誤信息的登入頁面
            return new RedirectView(FRONTEND_URL + "/login?error=true&message=" + e.getMessage());
        }
    }
} 