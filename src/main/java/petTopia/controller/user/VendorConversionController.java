package petTopia.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import jakarta.servlet.http.HttpSession;
import petTopia.model.user.Users;
import petTopia.service.user.VendorRegistrationService;
import java.util.Map;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/vendor")
public class VendorConversionController {
    
    private static final Logger logger = LoggerFactory.getLogger(VendorConversionController.class);
    
    @Autowired
    private VendorRegistrationService vendorRegistrationService;
    
    @PostMapping("/convert")
    public Map<String, Object> convertToVendor(
            @RequestParam(required = false) Boolean confirm,
            HttpSession session) {
        
        // 檢查是否已登入
        Users currentUser = (Users) session.getAttribute("loggedInUser");
        if (currentUser == null || currentUser.getUserRole() != Users.UserRole.MEMBER) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "請先登入會員帳號");
            return result;
        }
        
        try {
            // 檢查是否已有商家帳號
            Users existingVendor = vendorRegistrationService.findVendorByEmail(currentUser.getEmail());
            
            if (existingVendor != null) {
                // 如果已有商家帳號，直接切換
                // 清除舊的會員 session
                session.removeAttribute("loggedInUser");
                session.removeAttribute("userId");
                session.removeAttribute("userRole");
                session.removeAttribute("memberName");
                
                // 設置新的商家 session
                session.setAttribute("loggedInUser", existingVendor);
                session.setAttribute("userId", existingVendor.getId());
                session.setAttribute("userRole", existingVendor.getUserRole());
                session.setAttribute("vendorEmail", existingVendor.getEmail());
                session.setAttribute("provider", existingVendor.getProvider().name());
                
                Map<String, Object> result = new HashMap<>();
                result.put("success", true);
                result.put("message", "已切換至商家帳號");
                result.put("redirect", "/vendor/vendor_admin_profile");
                return result;
            }
            
            // 如果沒有商家帳號且未確認要註冊
            if (confirm == null || !confirm) {
                Map<String, Object> result = new HashMap<>();
                result.put("success", false);
                result.put("needConfirm", true);
                return result;
            }
            
            // 確認要註冊新商家帳號
            Map<String, Object> conversionResult = vendorRegistrationService.convertMemberToVendor(currentUser);
            
            if ((Boolean) conversionResult.get("success")) {
                Users newVendor = (Users) conversionResult.get("vendor");
                
                // 清除舊的會員 session
                session.removeAttribute("loggedInUser");
                session.removeAttribute("userId");
                session.removeAttribute("userRole");
                session.removeAttribute("memberName");
                
                // 設置新的商家 session
                session.setAttribute("loggedInUser", newVendor);
                session.setAttribute("userId", newVendor.getId());
                session.setAttribute("userRole", newVendor.getUserRole());
                session.setAttribute("vendorEmail", newVendor.getEmail());
                session.setAttribute("provider", newVendor.getProvider().name());
            }
            
            return conversionResult;
            
        } catch (Exception e) {
            logger.error("商家轉換失敗", e);
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "商家轉換失敗：" + e.getMessage());
            return result;
        }
    }
} 