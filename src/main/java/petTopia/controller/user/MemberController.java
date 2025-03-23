package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import petTopia.model.user.Member;
import petTopia.model.user.User;
import petTopia.service.user.MemberService;
import petTopia.service.user.MemberLoginService;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/member")
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    @Autowired
    private MemberService memberService;

    @Autowired
    private MemberLoginService memberLoginService;

    /**
     * 檢查名稱是否為郵箱格式
     */
    private boolean isEmailFormat(String name, String email) {
        if (name == null || email == null) return false;
        
        // 最基本的判斷：名稱與郵箱完全相同
        if (name.equalsIgnoreCase(email)) return true;
        
        // 更進階的判斷：名稱是否符合郵箱格式 (包含 @ 和 .)
        return name.matches("^[^@]+@[^@]+\\.[^@]+$");
    }
    
    /**
     * 獲取更友好的顯示名稱
     */
    private String getFriendlyDisplayName(String name, String email) {
        if (isEmailFormat(name, email)) {
            // 如果名稱是郵箱格式，使用郵箱的用戶名部分
            String emailUsername = email.split("@")[0];
            logger.info("名稱是郵箱格式，轉換為更友好的格式: {} -> {}", name, emailUsername);
            return emailUsername;
        }
        return name;
    }

    private byte[] processImage(MultipartFile file) throws IOException {
        // 讀取圖片
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        
        // 計算新的尺寸，保持寬高比
        int targetWidth = 800;
        int targetHeight = (int) (((double) originalImage.getHeight() / originalImage.getWidth()) * targetWidth);
        
        // 創建新的縮放後的圖片
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        resizedImage.createGraphics().drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        
        // 將圖片轉換為 byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        
        return baos.toByteArray();
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = memberLoginService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "用戶不存在"));
            }

            Integer userId = user.getId();
            Member member = memberService.getMemberById(userId);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "會員不存在"));
            }
            
            // 檢查名稱是否為郵箱格式，如果是則嘗試使用更友好的格式
            if (user.getProvider() != User.Provider.LOCAL) {
                // 對於第三方登入用戶，特別檢查名稱格式
                String currentName = member.getName();
                
                if (isEmailFormat(currentName, email)) {
                    // 如果目前名稱是郵箱格式，嘗試使用更友好的名稱
                    String friendlyName = getFriendlyDisplayName(currentName, email);
                    
                    if (!friendlyName.equals(currentName)) {
                        logger.info("更新會員資料中的名稱為更友好的格式: {} -> {}", currentName, friendlyName);
                        member.setName(friendlyName);
                        member.setUpdatedDate(LocalDateTime.now());
                        memberService.createOrUpdateMember(member);
                    }
                }
            }
            
            // 創建一個包含會員資料的 Map，以便添加額外資訊
            Map<String, Object> memberData = new HashMap<>();
            memberData.put("id", member.getId());
            memberData.put("name", member.getName());
            memberData.put("phone", member.getPhone());
            memberData.put("gender", member.getGender());
            memberData.put("address", member.getAddress());
            memberData.put("birthdate", member.getBirthdate());
            memberData.put("status", member.getStatus());
            memberData.put("updatedDate", member.getUpdatedDate());
            memberData.put("email", user.getEmail());
            memberData.put("provider", user.getProvider().toString());
            memberData.put("userRole", user.getUserRole().toString());
            
            return ResponseEntity.ok(memberData);
        } catch (Exception e) {
            logger.error("獲取會員資料失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲取會員資料失敗：" + e.getMessage()));
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Member member) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = memberLoginService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "用戶不存在"));
            }

            Integer userId = user.getId();
            
            // 獲取現有會員資料
            Member existingMember = memberService.getMemberById(userId);
            if (existingMember == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "會員不存在"));
            }
            
            // 更新資料（但不更新頭像，這會在單獨的端點中處理）
            existingMember.setName(member.getName());
            existingMember.setPhone(member.getPhone());
            existingMember.setGender(member.getGender());
            existingMember.setAddress(member.getAddress());
            
            // 如果有生日資料，則更新
            if (member.getBirthdate() != null) {
                existingMember.setBirthdate(member.getBirthdate());
            }
            
            existingMember.setUpdatedDate(LocalDateTime.now());
            
            memberService.createOrUpdateMember(existingMember);
            return ResponseEntity.ok(Map.of("message", "會員資料更新成功"));
        } catch (Exception e) {
            logger.error("更新會員資料失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "更新失敗：" + e.getMessage()));
        }
    }
    
    @PostMapping("/upload-photo")
    public ResponseEntity<?> uploadProfilePhoto(@RequestParam("photo") MultipartFile photo) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = memberLoginService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "用戶不存在"));
            }

            Integer userId = user.getId();
            
            // 獲取現有會員資料
            Member member = memberService.getMemberById(userId);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "會員不存在"));
            }
            
            // 處理並更新頭像
            if (photo != null && !photo.isEmpty()) {
                byte[] processedImage = processImage(photo);
                member.setProfilePhoto(processedImage);
                member.setUpdatedDate(LocalDateTime.now());
                
                memberService.createOrUpdateMember(member);
                return ResponseEntity.ok(Map.of("message", "頭像更新成功"));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "未提供頭像文件"));
            }
        } catch (Exception e) {
            logger.error("更新頭像失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "更新失敗：" + e.getMessage()));
        }
    }

    @GetMapping("/address")
    public ResponseEntity<?> getAddress() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = memberLoginService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "用戶不存在"));
            }

            Integer userId = user.getId();
            Member member = memberService.getMemberById(userId);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "會員不存在"));
            }
            
            return ResponseEntity.ok(Map.of("address", member.getAddress()));
        } catch (Exception e) {
            logger.error("獲取地址失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "獲取地址失敗：" + e.getMessage()));
        }
    }

    @GetMapping("/profile-photo")
    public ResponseEntity<byte[]> getProfilePhoto() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            
            User user = memberLoginService.findByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            Integer userId = user.getId();
            Member member = memberService.getMemberById(userId);
            if (member == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            
            byte[] photo = member.getProfilePhoto();
            if (photo == null) {
                photo = getDefaultAvatar();
            }
            
            if (photo.length == 0) {
                return ResponseEntity.noContent().build();
            }
            
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(photo);
        } catch (Exception e) {
            logger.error("獲取頭像失敗", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private byte[] getDefaultAvatar() throws IOException {
        // 返回空字节数组，让前端使用自己的默认头像
        return new byte[0];
    }
}
