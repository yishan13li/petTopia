package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import petTopia.model.user.MemberBean;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import petTopia.model.user.UsersBean;
import petTopia.service.user.MemberLoginService;
import petTopia.service.user.MemberService;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/member")
public class MemberController {
    
    @Autowired
    private MemberService memberService;
    
    @Autowired
    private MemberLoginService memberLoginService;
    
    // 顯示個人資料頁面的GET方法
    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "請先登入");
            return "redirect:/login";
        }
        
        try {
            MemberBean member = memberService.getMemberById(userId);
            // 獲取用戶的 email
            UsersBean user = memberLoginService.findById(userId);
            
            if (member == null) {
                // 4. 如果是新用戶，自動創建 member 資料
                member = new MemberBean();
                member.setId(userId);
                member.setStatus(false);
                member.setUser(user);
                member.setName(user.getEmail().split("@")[0]);
                member = memberService.createOrUpdateMember(member);
            }
            
            // 5. 將資料傳到頁面
            model.addAttribute("member", member);
            model.addAttribute("userEmail", user.getEmail());
            return "/member_profile";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "載入資料失敗: " + e.getMessage());
            return "redirect:/";
        }
    }
    
    // 更新會員資料
    @PostMapping("/update")
    public String updateProfile(
        @ModelAttribute MemberBean member,
        @RequestParam(value = "birthdate", required = false) String birthdateStr,
        @RequestParam(value = "photo", required = false) MultipartFile photo,
        HttpSession session,
        RedirectAttributes redirectAttributes) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                redirectAttributes.addFlashAttribute("error", "請先登入");
                return "redirect:/member/profile";
            }
            
            // 確保更新的是當前登入用戶的資料
            member.setId(userId);
            
            // 獲取當前用戶的 UsersBean
            UsersBean currentUser = memberLoginService.findById(userId);
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "用戶資料不存在");
                return "redirect:/member/profile";
            }
            member.setUser(currentUser);
            
            // 處理生日日期
            if (birthdateStr != null && !birthdateStr.isEmpty()) {
                member.setBirthdate(LocalDateTime.parse(birthdateStr + "T00:00:00"));
            }
            
            // 獲取現有會員資料
            MemberBean existingMember = memberService.getMemberById(userId);
            // 設置狀態
            if (existingMember != null) {
                member.setStatus(existingMember.getStatus());
            } else {
                member.setStatus(false);  // 新會員預設狀態
            }
            member.setUpdatedDate(LocalDateTime.now());
            
            // 驗證必填欄位
            if (member.getName() == null || member.getName().trim().isEmpty() ||
                member.getPhone() == null || member.getPhone().trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "姓名和電話為必填欄位");
                return "redirect:/member/profile";
            }
            
            // 處理頭像上傳
            if (photo != null && !photo.isEmpty()) {
                member.setProfilePhoto(photo.getBytes());
            } else if (existingMember != null && existingMember.getProfilePhoto() != null) {
                // 保留原有頭像
                member.setProfilePhoto(existingMember.getProfilePhoto());
            }
            
            // 保存或更新會員資料
            memberService.createOrUpdateMember(member);
            redirectAttributes.addFlashAttribute("success", "資料更新成功");
            
            return "redirect:/member/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "操作失敗：" + e.getMessage());
            return "redirect:/member/profile";
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

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        // 清除session中的用戶信息
        session.invalidate();
        
        // 添加登出成功消息（可選）
        redirectAttributes.addFlashAttribute("message", "已成功登出");
        
        // 重定向到首頁
        return "redirect:/index";
    }
}
