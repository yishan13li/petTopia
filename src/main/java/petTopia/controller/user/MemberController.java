package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import petTopia.model.user.MemberBean;
import petTopia.service.user.MemberService;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member")
public class MemberController {
    
    @Autowired
    private MemberService memberService;
    
    // 顯示會員資料頁面
    @GetMapping("/profile")
    public String showProfile(HttpSession session, Model model) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/member_login";
        }
        
        // 創建新的會員資料
        MemberBean member = new MemberBean();
        member.setId(userId);
        
        // 嘗試從資料庫獲取現有資料
        try {
            MemberBean existingMember = memberService.getMemberById(userId);
            if (existingMember != null) {
                member = existingMember;
            } else {
                model.addAttribute("isNewMember", true);
            }
        } catch (RuntimeException e) {
            model.addAttribute("isNewMember", true);
        }
        
        model.addAttribute("member", member);
        return "member_profile";
    }
    
    // 更新會員資料
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute MemberBean member, 
                              @RequestParam("photo") MultipartFile photo,
                              HttpSession session) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            member.setId(userId);
            if (!photo.isEmpty()) {
                member.setProfilePhoto(photo.getBytes());
            }
            // 根據是否存在決定創建或更新
            memberService.createOrUpdateMember(member);
            return "redirect:/member/profile?success";
        } catch (Exception e) {
            return "redirect:/member/profile?error=" + e.getMessage();
        }
    }
    
    // 刪除會員
    @PostMapping("/delete")
    public String deleteMember(HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            memberService.deleteMember(userId);
            session.invalidate();
        }
        return "redirect:/";
    }
}
