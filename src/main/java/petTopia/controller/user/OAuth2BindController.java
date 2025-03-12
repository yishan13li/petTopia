package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import petTopia.service.user.UsersService;
import petTopia.model.user.Users;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/oauth2")
public class OAuth2BindController {

    @Autowired
    private UsersService usersService;

    @GetMapping("/bind-confirm")
    public String showBindConfirmPage(HttpSession session, Model model) {
        // 檢查是否需要綁定
        Boolean bindingRequired = (Boolean) session.getAttribute("bindingRequired");
        if (bindingRequired == null || !bindingRequired) {
            return "redirect:/";
        }

        // 獲取綁定資訊
        String email = (String) session.getAttribute("oauthEmail");
        String provider = (String) session.getAttribute("oauthProvider");
        
        model.addAttribute("email", email);
        model.addAttribute("provider", provider);
        
        return "oauth2/bind_confirm";
    }

    @PostMapping("/bind")
    @ResponseBody
    public ResponseEntity<?> bindAccount(HttpSession session) {
        try {
            // 獲取綁定資訊
            Integer localUserId = (Integer) session.getAttribute("localUserId");
            String provider = (String) session.getAttribute("oauthProvider");
            
            // 執行帳號綁定
            usersService.bindOAuth2Account(localUserId, Users.Provider.valueOf(provider));
            
            // 清除綁定相關的 session 屬性
            session.removeAttribute("bindingRequired");
            session.removeAttribute("localUserId");
            session.removeAttribute("localUserRole");
            session.removeAttribute("oauthEmail");
            session.removeAttribute("oauthProvider");
            
            // 設置登入資訊
            session.setAttribute("userId", localUserId);
            session.setAttribute("userRole", session.getAttribute("localUserRole"));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "帳號綁定成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "帳號綁定失敗：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
} 