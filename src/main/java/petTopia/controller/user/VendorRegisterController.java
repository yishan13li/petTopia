package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import petTopia.model.user.Users;
import petTopia.model.user.Vendor;
import petTopia.service.user.EmailService;
import petTopia.service.user.VendorLoginService;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
@RequestMapping("/vendor")
public class VendorRegisterController {

    private static final Logger logger = LoggerFactory.getLogger(VendorRegisterController.class);

    @Autowired
    private VendorLoginService vendorService;

    @Autowired
    private EmailService emailService;

    private Map<String, Map<String, Object>> verificationCodes = new HashMap<>();

    @GetMapping("/vendor_register")
    public String showRegisterForm(Model model) {
        model.addAttribute("errors", new HashMap<String, String>());
        return "vendor/vendor_register";
    }

    @PostMapping("/vendor_register")
    public String processRegister(
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String password,
            Model model) {
        
        Map<String, String> errors = new HashMap<>();
        model.addAttribute("errors", errors);

        try {
            // 检查是否已存在相同email的商家账号
            Users existingVendor = vendorService.findByEmail(email);
            if (existingVendor != null) {
                errors.put("registerFailed", "此 email 已註冊為商家");
                return "vendor/vendor_register";
            }

            // 1. 创建用户基本信息
            Users newUser = new Users();
            newUser.setEmail(email);
            newUser.setPassword(password);
            newUser.setUserRole(Users.UserRole.VENDOR);
            newUser.setEmailVerified(true); // 因為已經通過驗證碼驗證

            // 2. 创建商家信息
            Vendor newVendor = new Vendor();
            newVendor.setRegistrationDate(LocalDateTime.now());
            newVendor.setUpdatedDate(LocalDateTime.now());
            newVendor.setStatus(true);  // 設置為啟用狀態
            newVendor.setVendorCategoryId(1);

            // 3. 使用 VendorLoginService 处理注册
            Map<String, Object> result = vendorService.registerVendor(newUser, newVendor);
            
            if ((Boolean) result.get("success")) {
                return "redirect:/vendor/vendor_login?registered=true";
            } else {
                errors.put("registerFailed", (String) result.get("message"));
                return "vendor/vendor_register";
            }
            
        } catch (Exception e) {
            errors.put("registerFailed", "註冊失敗：" + e.getMessage());
            return "vendor/vendor_register";
        }
    }

    @PostMapping("/api/register")
    @ResponseBody
    public Map<String, Object> apiRegister(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // 基本驗證
            if (!request.get("password").equals(request.get("confirmPassword"))) {
                response.put("success", false);
                response.put("message", "密碼與確認密碼不符");
                return response;
            }

            Users newUser = new Users();
            newUser.setEmail(request.get("email"));
            newUser.setPassword(request.get("password"));
            newUser.setUserRole(Users.UserRole.VENDOR);
            
            // 2. 創建商家信息
            Vendor newVendor = new Vendor();
            newVendor.setRegistrationDate(LocalDateTime.now());
            newVendor.setUpdatedDate(LocalDateTime.now());
            newVendor.setStatus(false);
            newVendor.setVendorCategoryId(1);  // 設置預設分類為1

            // 3. 使用 VendorLoginService 處理註冊
            Map<String, Object> result = vendorService.registerVendor(newUser, newVendor);
            
            if ((Boolean) result.get("success")) {
                response.put("success", true);
                response.put("message", "註冊成功");
            } else {
                response.put("success", false);
                response.put("message", (String) result.get("message"));
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", e.getMessage());
        }
        
        return response;
    }

    @PostMapping("/api/send-verification")
    @ResponseBody
    public Map<String, Object> apiSendVerificationCode(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String email = request.get("email");
            // 生成6位數驗證碼
            String code = String.format("%06d", new Random().nextInt(1000000));
            
            // 存儲驗證碼和時間戳
            Map<String, Object> codeData = new HashMap<>();
            codeData.put("code", code);
            codeData.put("timestamp", LocalDateTime.now());
            verificationCodes.put(email, codeData);
            
            // 發送驗證碼到郵箱
            emailService.sendVerificationCode(email, code);
            
            response.put("success", true);
            response.put("message", "驗證碼已發送");
        } catch (Exception e) {
            logger.error("發送驗證碼失敗", e);
            response.put("success", false);
            response.put("message", "驗證碼發送失敗：" + e.getMessage());
        }
        
        return response;
    }

    @PostMapping("/verify-code")
    @ResponseBody
    public Map<String, Object> verifyCode(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        String email = request.get("email");
        String code = request.get("code");

        // 檢查驗證碼是否存在
        Map<String, Object> storedData = verificationCodes.get(email);
        if (storedData == null || !code.equals(storedData.get("code"))) {
            response.put("success", false);
            response.put("message", "驗證碼錯誤");
            return response;
        }

        // 檢查驗證碼是否過期（5分鐘）
        LocalDateTime codeTime = (LocalDateTime) storedData.get("timestamp");
        if (codeTime.plusMinutes(5).isBefore(LocalDateTime.now())) {
            verificationCodes.remove(email);
            response.put("success", false);
            response.put("message", "驗證碼已過期");
            return response;
        }

        try {
            // 檢查是否已經是商家帳號
            Users existingVendor = vendorService.findByEmail(email);
            if (existingVendor != null) {
                response.put("success", false);
                response.put("message", "此 email 已註冊為商家帳號");
                return response;
            }

            // 驗證成功，清除驗證碼
            verificationCodes.remove(email);
            response.put("success", true);
            response.put("message", "驗證成功");
            
        } catch (Exception e) {
            logger.error("驗證碼驗證失敗", e);
            response.put("success", false);
            response.put("message", "驗證失敗：" + e.getMessage());
        }
        
        return response;
    }
} 