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
import petTopia.service.user.MemberLoginService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller
@RequestMapping("/member")
public class MemberRegisterController {

    @Autowired
    private MemberLoginService memberService;

    @Autowired
    private EmailService emailService;

    private Map<String, Map<String, Object>> verificationCodes = new HashMap<>();

    @GetMapping("/member_register")
    public String showRegisterPage(Model model) {
        model.addAttribute("errors", new HashMap<String, String>());
        return "member_register";
    }

    @PostMapping("/member_register/controller")
    public String processRegister(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            Model model) {
        
        Map<String, String> errors = new HashMap<>();
        model.addAttribute("errors", errors);

        try {
            UsersBean newUser = new UsersBean();
            newUser.setEmail(email);
            newUser.setPassword(password);
            memberService.register(newUser);
            return "redirect:/member_login?registered=true";
        } catch (Exception e) {
            errors.put("registerFailed", "註冊失敗：" + e.getMessage());
            return "member_register";
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