package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class OAuth2LoginController {

    /*
    @Autowired
    private VendorLoginService vendorService;
    */

    // 原本的 OAuth2 登入處理方法（暫時註解）
    /*
    @GetMapping("/oauth2/callback")
    public String handleOAuth2Callback(OAuth2AuthenticationToken authentication) {
        OAuth2User oauth2User = authentication.getPrincipal();
        String provider = authentication.getAuthorizedClientRegistrationId();
        
        String email = oauth2User.getAttribute("email");
        
        // 檢查用戶是否已存在
        UsersBean existingUser = vendorService.findByEmail(email);
        if (existingUser == null) {
            // 創建新用戶
            UsersBean newUser = new UsersBean();
            newUser.setEmail(email);
            newUser.setUserRole(UsersBean.UserRole.VENDOR);
            // 設置一個隨機密碼或使用OAuth2提供者的ID
            newUser.setPassword(provider + "_" + oauth2User.getName());
            vendorService.register(newUser);
        }
        
        return "redirect:/vendor_login?oauth2Success=true";
    }
    */

    // 簡化版的處理方法
    @GetMapping("/oauth2/callback")
    public String handleOAuth2Callback(OAuth2AuthenticationToken authentication) {
        // 暫時跳過 OAuth2 註冊登入流程
        return "redirect:/vendor_login";
    }
} 