package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.security.crypto.password.PasswordEncoder;

import petTopia.model.user.Member;
import petTopia.model.user.Users;
import petTopia.service.user.MemberLoginService;
import petTopia.service.user.MemberService;
import petTopia.util.SessionManager;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.Map;
import java.util.HashMap;
import java.util.Base64;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Controller
public class MemberController {

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberLoginService memberLoginService;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private SessionManager sessionManager;

    // 新增圖片處理相關的常量
    private static final int MAX_IMAGE_SIZE = 1024 * 1024; // 1MB
    private static final int TARGET_WIDTH = 300;
    private static final String IMAGE_FORMAT = "JPEG";

    // 新增圖片處理方法
    private byte[] processImage(MultipartFile photo) throws Exception {
        if (photo == null || photo.isEmpty()) {
            return null;
        }

        byte[] photoData = photo.getBytes();
        
        // 如果圖片小於1MB且是JPEG格式，直接返回
        if (photoData.length <= MAX_IMAGE_SIZE && 
            photo.getContentType() != null && 
            photo.getContentType().equals(MediaType.IMAGE_JPEG_VALUE)) {
            return photoData;
        }

        // 讀取原始圖片
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(photoData));
        if (originalImage == null) {
            throw new IllegalArgumentException("無效的圖片格式");
        }

        // 計算新的尺寸，保持比例
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        int targetHeight = (int) (originalHeight * (TARGET_WIDTH / (double) originalWidth));

        // 創建縮圖
        BufferedImage resizedImage = new BufferedImage(TARGET_WIDTH, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, TARGET_WIDTH, targetHeight, null);
        g.dispose();

        // 轉換為 byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, IMAGE_FORMAT, baos);
        return baos.toByteArray();
    }

    // 新增圖片獲取端點
    @GetMapping("/api/member/profile-photo")
    public ResponseEntity<byte[]> getProfilePhoto(HttpSession session) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                return ResponseEntity.badRequest().build();
            }

            Member member = memberService.getMemberById(userId);
            if (member != null && member.getProfilePhoto() != null) {
                return ResponseEntity.ok()
                    .header("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
                    .header("Pragma", "no-cache")
                    .header("Expires", "0")
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(member.getProfilePhoto());
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    // 顯示個人資料頁面
    @GetMapping("/profile")
    public String showProfile(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "請先登入");
            return "redirect:/login";
        }

        try {
            Member member = memberService.getMemberById(userId);
            Users user = memberLoginService.findById(userId);

            if (member == null) {
                member = new Member();
                member.setId(userId);
                member.setStatus(false);
                member.setUser(user);
                member.setName(user.getEmail().split("@")[0]);
                member = memberService.createOrUpdateMember(member, session);
            }

            model.addAttribute("member", member);
            model.addAttribute("userEmail", user.getEmail());
            return "profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "載入資料失敗: " + e.getMessage());
            return "redirect:/";
        }
    }

    // 修改更新會員資料的方法
    @PostMapping("/update")
    public String updateProfile(
            @ModelAttribute Member member,
            @RequestParam(value = "birthdate", required = false) String birthdateStr,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                redirectAttributes.addFlashAttribute("error", "請先登入");
                return "redirect:/profile";
            }

            member.setId(userId);
            Users currentUser = memberLoginService.findById(userId);
            if (currentUser == null) {
                redirectAttributes.addFlashAttribute("error", "用戶資料不存在");
                return "redirect:/profile";
            }
            member.setUser(currentUser);

            // 處理生日日期
            if (birthdateStr != null && !birthdateStr.trim().isEmpty()) {
                try {
                    // 直接解析為 LocalDate
                    LocalDate birthdate = LocalDate.parse(birthdateStr);
                    member.setBirthdate(birthdate);
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", "生日日期格式錯誤");
                    return "redirect:/profile";
                }
            } else {
                member.setBirthdate(null);
            }

            Member existingMember = memberService.getMemberById(userId);
            if (existingMember != null) {
                member.setStatus(existingMember.getStatus());
                if (photo == null || photo.isEmpty()) {
                    member.setProfilePhoto(existingMember.getProfilePhoto());
                }
            } else {
                member.setStatus(false);
            }
            member.setUpdatedDate(LocalDateTime.now());

            // 處理圖片
            if (photo != null && !photo.isEmpty()) {
                try {
                    byte[] processedImage = processImage(photo);
                    member.setProfilePhoto(processedImage);
                } catch (Exception e) {
                    redirectAttributes.addFlashAttribute("error", "圖片處理失敗: " + e.getMessage());
                    return "redirect:/profile";
                }
            }

            memberService.createOrUpdateMember(member, session);
            
            // 更新session中的會員名稱
            String displayName = member.getName();
            session.setAttribute("memberName", displayName);

            redirectAttributes.addFlashAttribute("success", "資料更新成功");
            return "redirect:/profile";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "更新失敗: " + e.getMessage());
            return "redirect:/profile";
        }
    }

    // 顯示地址管理頁面
    @GetMapping("/address")
    public String showAddress(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "請先登入");
            return "redirect:/login";
        }

        try {
            Member member = memberService.getMemberById(userId);
            if (member != null && member.getProfilePhoto() != null) {
                String photoBase64 = Base64.getEncoder().encodeToString(member.getProfilePhoto());
                model.addAttribute("memberProfilePhotoBase64", photoBase64);
                session.setAttribute("memberProfilePhotoBase64", photoBase64);
            }
            model.addAttribute("member", member);
            return "address";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "載入地址資料失敗");
            return "redirect:/profile";
        }
    }

    // 顯示優惠券列表頁面
    @GetMapping("/coupons")
    public String showCoupons(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "請先登入");
            return "redirect:/login";
        }

        try {
            Member member = memberService.getMemberById(userId);
            if (member != null && member.getProfilePhoto() != null) {
                String photoBase64 = Base64.getEncoder().encodeToString(member.getProfilePhoto());
                model.addAttribute("memberProfilePhotoBase64", photoBase64);
                session.setAttribute("memberProfilePhotoBase64", photoBase64);
            }
            model.addAttribute("member", member);
            // TODO: 添加優惠券列表
            return "coupon_list";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "載入優惠券資料失敗");
            return "redirect:/profile";
        }
    }

    // 顯示密碼修改頁面
    @GetMapping("/password")
    public String showPasswordForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId == null) {
            redirectAttributes.addFlashAttribute("error", "請先登入");
            return "redirect:/login";
        }
        return "password";
    }

    // 更新密碼
    @PostMapping("/update-password")
    @ResponseBody
    public ResponseEntity<?> updatePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId == null) {
                response.put("success", false);
                response.put("message", "請先登入");
                return ResponseEntity.badRequest().body(response);
            }

            Users user = memberLoginService.findById(userId);
            if (user == null) {
                response.put("success", false);
                response.put("message", "用戶不存在");
                return ResponseEntity.badRequest().body(response);
            }

            // 驗證當前密碼
            if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                response.put("success", false);
                response.put("message", "當前密碼錯誤");
                return ResponseEntity.badRequest().body(response);
            }

            // 驗證新密碼
            if (!newPassword.equals(confirmPassword)) {
                response.put("success", false);
                response.put("message", "新密碼與確認密碼不符");
                return ResponseEntity.badRequest().body(response);
            }

            // 更新密碼
            user.setPassword(passwordEncoder.encode(newPassword));
            memberLoginService.updateUser(user);

            response.put("success", true);
            response.put("message", "密碼更新成功");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "密碼更新失敗：" + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // 刪除會員
    @PostMapping("/delete")
    public String deleteMember(HttpSession session, RedirectAttributes redirectAttributes) {
        Integer userId = (Integer) session.getAttribute("userId");
        if (userId != null) {
            try {
                memberService.deleteMember(userId, session);
                redirectAttributes.addFlashAttribute("success", "帳號已成功刪除");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("error", "刪除帳號失敗：" + e.getMessage());
            }
        }
        return "redirect:/";
    }
}
