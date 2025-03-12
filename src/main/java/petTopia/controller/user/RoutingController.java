package petTopia.controller.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.util.HashMap;

@Controller
public class RoutingController {
    @GetMapping("/secure/login")
    public String login() {
        return "secure/login";
    }
    
    @GetMapping("/pages/product")
    public String product() {
        return "pages/product";
    }
    
    @GetMapping("/pages/display")
    public String display() {
        return "pages/display";
    }
    
    @GetMapping("/vendor")
    public String showVendorPage() {
        return "vendor_login";
    }
    
    @GetMapping("/vendor_register")
    public String showVendorRegisterPage(Model model) {
        model.addAttribute("errors", new HashMap<String, String>());
        return "vendor_register";
    }
}