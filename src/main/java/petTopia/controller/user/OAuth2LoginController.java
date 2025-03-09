package petTopia.controller.user;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import petTopia.model.user.Users;
import petTopia.model.user.Vendor;
import petTopia.model.user.Member;
import petTopia.service.user.VendorLoginService;
import petTopia.service.user.MemberLoginService;

@Controller
public class OAuth2LoginController {

    @Autowired
    private VendorLoginService vendorService;
    
    @Autowired
    private MemberLoginService memberService;

    @GetMapping("/oauth2/callback/vendor")
    public String handleVendorOAuth2Callback(OAuth2AuthenticationToken authentication) throws UnsupportedEncodingException {
        OAuth2User oauth2User = authentication.getPrincipal();
        String provider = authentication.getAuthorizedClientRegistrationId();
        String email = oauth2User.getAttribute("email");
        
        // 检查是否已经存在相同email的商家账号
        Users existingUser = vendorService.findByEmail(email);
        if (existingUser != null && existingUser.getUserRole() == Users.UserRole.VENDOR) {
            // 如果已存在相同email的商家账号，直接登录
            return "redirect:/vendor/login?email=" + email + "&oauth2Success=true";
        }
        
        // 创建新商家账号
        Users newUser = new Users();
        newUser.setEmail(email);
        newUser.setUserRole(Users.UserRole.VENDOR);
        newUser.setPassword(provider + "_" + oauth2User.getName());
        
        Vendor newVendor = new Vendor();
        vendorService.registerVendor(newUser, newVendor);
        
        return "redirect:/vendor/login?oauth2Success=true";
    }

    @GetMapping("/oauth2/callback/member")
    public String handleMemberOAuth2Callback(OAuth2AuthenticationToken authentication) throws UnsupportedEncodingException {
        OAuth2User oauth2User = authentication.getPrincipal();
        String provider = authentication.getAuthorizedClientRegistrationId();
        String email = oauth2User.getAttribute("email");
        
        // 检查是否已经存在相同email的会员账号
        Users existingUser = memberService.findByEmail(email);
        if (existingUser != null && existingUser.getUserRole() == Users.UserRole.MEMBER) {
            // 如果已存在相同email的会员账号，直接登录
            return "redirect:/member/login?oauth2Success=true&message=" + URLEncoder.encode("OAuth2 登入成功", "UTF-8");
        }
        
        // 创建新会员账号
        Users newUser = new Users();
        newUser.setEmail(email);
        newUser.setUserRole(Users.UserRole.MEMBER);
        newUser.setPassword(provider + "_" + oauth2User.getName());
        
        Member newMember = new Member();
        memberService.registerMember(newUser, newMember);
        
        return "redirect:/member/login?oauth2Success=true";
    }
} 