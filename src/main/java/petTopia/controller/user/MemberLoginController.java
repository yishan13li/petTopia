package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import petTopia.model.user.Users;
import petTopia.service.user.MemberLoginService;
import petTopia.model.user.Member;
import petTopia.service.user.MemberService;
import petTopia.util.SessionManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

@Controller
public class MemberLoginController {
    private static final Logger logger = LoggerFactory.getLogger(MemberLoginController.class);

    @Autowired
    private MemberLoginService memberLoginService;

    @Autowired
    private MemberService memberService;
    
    @Autowired
    private SessionManager sessionManager;

    @GetMapping("/login")
    public String showLoginPage(Model model, @RequestParam(required = false) String registered) {
        Map<String, String> messages = new HashMap<>();
        if (registered != null && registered.equals("true")) {
            messages.put("success", "註冊成功，請登入");
        }
        model.addAttribute("messages", messages);
        model.addAttribute("errors", new HashMap<String, String>());
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) throws UnsupportedEncodingException {
        Users user = (Users) session.getAttribute("loggedInUser");
        if (user != null) {
            logger.info("會員登出 - userId: {}, email: {}", user.getId(), user.getEmail());
        }
        
        sessionManager.clearSession(session);
        
        return "redirect:/login?logout=true&message=" + URLEncoder.encode("登出成功", "UTF-8");
    }

    @PostMapping("/login")
    public String login(@RequestParam String email, @RequestParam String password,
            HttpSession session, RedirectAttributes redirectAttributes) {
        try {
            Map<String, Object> loginResult = memberLoginService.memberLogin(email, password);
            
            // 檢查是否是第三方登入帳號
            if (loginResult.containsKey("isThirdPartyAccount") && (Boolean) loginResult.get("isThirdPartyAccount")) {
                Users.Provider provider = (Users.Provider) loginResult.get("provider");
                redirectAttributes.addFlashAttribute("error", 
                    "此帳號是使用" + provider.toString() + "註冊的，請使用對應的登入方式，或點擊下方連結設定本地密碼");
                redirectAttributes.addFlashAttribute("showLocalPasswordSetup", true);
                redirectAttributes.addFlashAttribute("userEmail", email);
                redirectAttributes.addFlashAttribute("provider", provider.toString());
                return "redirect:/login";
            }
            
            if ((Boolean) loginResult.get("success")) {
                Users user = (Users) loginResult.get("user");
                session.setAttribute("userId", user.getId());
                
                Member member = memberService.getMemberById(user.getId());
                String displayName = (member != null && member.getName() != null) ? 
                    member.getName() : user.getEmail().split("@")[0];
                
                sessionManager.updateMemberInfo(session, displayName, user.getEmail());
                
                if (member != null && member.getProfilePhoto() != null) {
                    sessionManager.updateProfilePhoto(session, member.getProfilePhoto());
                }
                
                return "redirect:/";
            } else {
                redirectAttributes.addFlashAttribute("error", loginResult.get("message"));
                return "redirect:/login";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "登入失敗: " + e.getMessage());
            return "redirect:/login";
        }
    }
}
