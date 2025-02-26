package petTopia.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.util.HashMap;

@Controller
public class RoutingController {
    @GetMapping("/secure/login")
    public String login() {
        return "secure/login"; // 去掉前綴斜杠
    }
    
    @GetMapping("/pages/product")
    public String product() {
        return "pages/product"; // 去掉前綴斜杠
    }
    
    @GetMapping("/pages/display")
    public String display() {
        return "pages/display"; // 去掉前綴斜杠
    }
    
    @GetMapping("/vendor")
    public String showVendorPage() {
        return "vendor_login";  // 確保視圖名稱也更新
    }
    
    @GetMapping("/vendor_register")
    public String showVendorRegisterPage(Model model) {
        model.addAttribute("errors", new HashMap<String, String>());
        return "vendor_register";
    }
    
    @GetMapping("/member_profile")
    public String showMemberProfile() {
        return "member_profile";
    }
}