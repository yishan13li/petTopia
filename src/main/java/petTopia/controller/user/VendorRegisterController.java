package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import petTopia.model.user.UsersBean;
import petTopia.service.user.EmailService;
import petTopia.service.user.VendorLoginService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("/vendor")
public class VendorRegisterController {

    @Autowired
    private VendorLoginService vendorService;

    @Autowired
    private EmailService emailService;

    // 存儲驗證碼的 Map (email -> {code, timestamp})
    private Map<String, Map<String, Object>> verificationCodes = new HashMap<>();

    @GetMapping("/vendor_register")
    public String showRegisterPage(Model model) {
        model.addAttribute("errors", new HashMap<String, String>());
        return "vendor_register";
    }

    @PostMapping("/vendor_register/controller")
    public String processRegister(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            @RequestParam(required = false) String confirmPassword,
            @RequestParam(required = false) String verificationCode,
            Model model) {

        Map<String, String> errors = new HashMap<>();
        model.addAttribute("errors", errors);

        // 驗證 email
        if (email == null || email.trim().isEmpty()) {
            errors.put("email", "請輸入電子郵件");
        } else if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            errors.put("email", "請輸入有效的電子郵件格式");
        }

        // 驗證密碼
        if (password == null || password.trim().isEmpty()) {
            errors.put("password", "請輸入密碼");
        } else if (password.length() < 6) {
            errors.put("password", "密碼長度至少需要6個字符");
        }

        // 驗證確認密碼
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            errors.put("confirmPassword", "請再次輸入密碼");
        } else if (!confirmPassword.equals(password)) {
            errors.put("confirmPassword", "兩次輸入的密碼不一致");
        }

        // 驗證驗證碼
        Map<String, Object> codeData = verificationCodes.get(email);
        if (codeData == null || !((String) codeData.get("code")).equals(verificationCode)) {
            errors.put("verificationCode", "驗證碼無效或已過期");
            return "vendor_register";
        }

        // 檢查郵箱是否已被註冊
        if (vendorService.findByEmail(email) != null) {
            errors.put("email", "此郵箱已被註冊");
            return "vendor_register";
        }

        // 如果有錯誤，返回註冊頁面
        if (!errors.isEmpty()) {
            return "vendor_register";
        }

        try {
            // 創建新用戶
            UsersBean newUser = new UsersBean();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setUserRole(UsersBean.UserRole.VENDOR); // 設置為商家角色

            // 調用 service 層進行註冊
            UsersBean registeredUser = vendorService.register(newUser);

            if (registeredUser != null) {
                // 註冊成功，重定向到登入頁面
                return "redirect:/vendor_login?registered=true";
            } else {
                errors.put("registerFailed", "註冊失敗，請稍後再試");
                return "vendor_register";
            }

        } catch (Exception e) {
            errors.put("systemError", "系統發生錯誤，請稍後再試");
            return "vendor_register";
        } finally {
            // 註冊完成後清除驗證碼
            verificationCodes.remove(email);
        }
    }

    // 發送驗證碼
    @PostMapping("/send-verification")
    @ResponseBody
    public Map<String, Object> sendVerificationCode(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 生成6位數驗證碼
            String code = String.format("%06d", new Random().nextInt(1000000));
            
            // 存儲驗證碼和時間戳
            Map<String, Object> codeData = new HashMap<>();
            codeData.put("code", code);
            codeData.put("timestamp", System.currentTimeMillis());
            verificationCodes.put(email, codeData);
            
            // 發送驗證碼到郵箱
            emailService.sendVerificationCode(email, code);
            
            response.put("success", true);
            response.put("message", "驗證碼已發送");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "驗證碼發送失敗");
        }
        
        return response;
    }
    
    // 驗證驗證碼
    @PostMapping("/verify-code")
    @ResponseBody
    public Map<String, Object> verifyCode(
            @RequestParam String email,
            @RequestParam String code) {
        Map<String, Object> response = new HashMap<>();
        
        Map<String, Object> codeData = verificationCodes.get(email);
        if (codeData == null) {
            response.put("success", false);
            response.put("message", "驗證碼已過期");
            return response;
        }
        
        String storedCode = (String) codeData.get("code");
        long timestamp = (long) codeData.get("timestamp");
        
        // 檢查驗證碼是否過期（5分鐘）
        if (System.currentTimeMillis() - timestamp > 5 * 60 * 1000) {
            verificationCodes.remove(email);
            response.put("success", false);
            response.put("message", "驗證碼已過期");
            return response;
        }
        
        if (!code.equals(storedCode)) {
            response.put("success", false);
            response.put("message", "驗證碼錯誤");
            return response;
        }
        
        response.put("success", true);
        response.put("message", "驗證成功");
        return response;
    }
} 