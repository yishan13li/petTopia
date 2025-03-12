package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import petTopia.model.user.Users;
import petTopia.service.user.VendorRegistrationService;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.net.URLEncoder;

@Controller
@RequestMapping("/vendor")
public class VendorRegisterController {

    @Autowired
    private VendorRegistrationService vendorRegistrationService;

    @GetMapping("/vendor_register")
    public String showRegisterPage(Model model) {
        model.addAttribute("errors", new HashMap<String, String>());
        return "vendor/vendor_register";
    }

    @PostMapping("/vendor_register")
    public String register(@RequestParam String email,
                         @RequestParam String password,
                         @RequestParam String confirmPassword,
                         Model model) throws UnsupportedEncodingException {
        
        Map<String, String> errors = new HashMap<>();
        model.addAttribute("errors", errors);

        try {
            // 基本驗證
            if (!password.equals(confirmPassword)) {
                errors.put("registerFailed", "密碼與確認密碼不符");
                return "vendor/vendor_register";
            }

            // 檢查是否已存在相同email的商家帳號
            Users existingVendor = vendorRegistrationService.findByEmail(email);
            if (existingVendor != null) {
                errors.put("registerFailed", "此 email 已註冊為商家");
                return "vendor/vendor_register";
            }

            // 創建用戶基本信息
            Users newUser = new Users();
            newUser.setEmail(email);
            newUser.setPassword(password);

            // 使用註冊服務處理註冊
            vendorRegistrationService.register(newUser);
            
            return "redirect:/vendor/vendor_login?registered=true&message=" + 
                   URLEncoder.encode("註冊成功，請查收驗證郵件後登入", "UTF-8");
            
        } catch (Exception e) {
            errors.put("registerFailed", "註冊失敗：" + e.getMessage());
            return "vendor/vendor_register";
        }
    }

    @GetMapping("/verify")
    public String verifyEmail(@RequestParam String token, Model model) throws UnsupportedEncodingException {
        boolean verified = vendorRegistrationService.verifyEmail(token);
        
        if (verified) {
            return "redirect:/vendor/vendor_login?verified=true&message=" + 
                   URLEncoder.encode("驗證成功，請登入", "UTF-8");
        } else {
            return "redirect:/vendor/vendor_register?error=true&message=" + 
                   URLEncoder.encode("驗證失敗，請重新註冊", "UTF-8");
        }
    }
} 