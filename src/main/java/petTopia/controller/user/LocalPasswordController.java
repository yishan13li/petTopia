package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import petTopia.model.user.Users;
import petTopia.service.user.EmailService;
import petTopia.service.user.MemberLoginService;
import petTopia.repository.user.UsersRepository; 

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/local-password")
public class LocalPasswordController {
    
    private static final Logger logger = LoggerFactory.getLogger(LocalPasswordController.class);
    
    @Autowired
    private MemberLoginService memberLoginService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private Map<String, Map<String, Object>> verificationTokens = new HashMap<>();
    
    @GetMapping("/setup")
    public String showSetupPage(@RequestParam String email, Model model) {
        Users user = memberLoginService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "找不到會員帳號");
            return "local-password-error";
        }
        
        if (user.getProvider() == Users.Provider.LOCAL) {
            model.addAttribute("error", "此帳號已是本地帳號");
            return "local-password-error";
        }
        
        model.addAttribute("email", email);
        model.addAttribute("provider", user.getProvider());
        return "local-password-setup";
    }
    
    @PostMapping("/send-verification")
    @ResponseBody
    public Map<String, Object> sendVerification(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Users user = memberLoginService.findByEmail(email);
            if (user == null) {
                response.put("success", false);
                response.put("message", "找不到會員帳號");
                return response;
            }
            
            if (user.getProvider() == Users.Provider.LOCAL) {
                response.put("success", false);
                response.put("message", "此帳號已是本地帳號");
                return response;
            }
            
            // 生成驗證令牌
            String token = UUID.randomUUID().toString();
            Map<String, Object> tokenData = new HashMap<>();
            tokenData.put("email", email);
            tokenData.put("timestamp", System.currentTimeMillis());
            verificationTokens.put(token, tokenData);
            
            // 發送驗證郵件
            String verificationLink = "http://localhost:8080/local-password/verify?token=" + token;
            String emailContent = String.format(
                "親愛的用戶您好，<br><br>" +
                "您正在為您的第三方登入帳號設置本地密碼。<br>" +
                "請點擊以下連結進行驗證：<br><br>" +
                "<a href='%s'>點擊這裡設置密碼</a><br><br>" +
                "此連結將在30分鐘後失效。<br><br>" +
                "如果這不是您的操作，請忽略此郵件。<br><br>" +
                "謝謝！<br>PetTopia團隊",
                verificationLink
            );
            
            emailService.sendHtmlEmail(email, "設置本地密碼驗證", emailContent);
            
            response.put("success", true);
            response.put("message", "驗證郵件已發送，請查收");
            
        } catch (Exception e) {
            logger.error("發送驗證郵件失敗", e);
            response.put("success", false);
            response.put("message", "發送驗證郵件失敗：" + e.getMessage());
        }
        
        return response;
    }
    
    @GetMapping("/verify")
    public String verifyToken(@RequestParam String token, Model model) {
        Map<String, Object> tokenData = verificationTokens.get(token);
        
        if (tokenData == null) {
            model.addAttribute("error", "無效的驗證連結");
            return "local-password-error";
        }
        
        // 檢查令牌是否過期（30分鐘）
        long timestamp = (long) tokenData.get("timestamp");
        if (System.currentTimeMillis() - timestamp > 30 * 60 * 1000) {
            verificationTokens.remove(token);
            model.addAttribute("error", "驗證連結已過期");
            return "local-password-error";
        }
        
        model.addAttribute("token", token);
        model.addAttribute("email", tokenData.get("email"));
        return "local-password-form";
    }
    
    @PostMapping("/set-password")
    @ResponseBody
    public Map<String, Object> setPassword(@RequestParam String token, @RequestParam String password) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Map<String, Object> tokenData = verificationTokens.get(token);
            if (tokenData == null) {
                response.put("success", false);
                response.put("message", "無效的請求");
                return response;
            }
            
            String email = (String) tokenData.get("email");
            Users user = memberLoginService.findByEmail(email);
            
            if (user == null) {
                response.put("success", false);
                response.put("message", "找不到用戶");
                return response;
            }
            
            // 設置本地密碼並保留原有的第三方登入提供者
            user.setPassword(passwordEncoder.encode(password));
            user.setLocalEnabled(true); // 啟用本地密碼登入
            usersRepository.save(user);
            
            // 清除驗證令牌
            verificationTokens.remove(token);
            
            response.put("success", true);
            response.put("message", "密碼設置成功，請使用新密碼登入");
            
        } catch (Exception e) {
            logger.error("設置密碼失敗", e);
            response.put("success", false);
            response.put("message", "設置密碼失敗：" + e.getMessage());
        }
        
        return response;
    }
} 