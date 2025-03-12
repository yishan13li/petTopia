package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import petTopia.model.user.Users;
import petTopia.service.user.RegistrationService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    // 檢查 email 是否已存在
    @GetMapping("/check-email")
    @ResponseBody
    public Map<String, Object> checkEmail(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();
        try {
            String trimmedEmail = email.toLowerCase().trim();
            Users existingUser = registrationService.findByEmail(trimmedEmail);
            
            if (existingUser != null) {
                String message;
                if (existingUser.getProvider() == Users.Provider.GOOGLE) {
                    message = "此 email 已使用 Google 帳號登入過，請點擊「使用 Google 登入」按鈕";
                } else if (existingUser.getUserRole() == Users.UserRole.VENDOR) {
                    message = "此 email 已註冊為商家帳號，請使用其他 email 註冊會員";
                } else {
                    message = "此 email 已註冊為會員帳號，請直接登入";
                }
                response.put("exists", true);
                response.put("message", message);
            } else {
                response.put("exists", false);
            }
        } catch (Exception e) {
            response.put("error", "檢查 email 時發生錯誤");
        }
        return response;
    }
} 