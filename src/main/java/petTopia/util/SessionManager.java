package petTopia.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Component
public class SessionManager {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 更新用戶頭像
     */
    public void updateProfilePhoto(HttpSession session, byte[] photoData) {
        if (photoData != null) {
            String photoBase64 = Base64.getEncoder().encodeToString(photoData);
            session.setAttribute("memberProfilePhotoBase64", photoBase64);
            session.setAttribute("photoVersion", System.currentTimeMillis());
        } else {
            session.removeAttribute("memberProfilePhotoBase64");
            session.removeAttribute("photoVersion");
        }
    }
    
    /**
     * 更新會員資訊
     */
    public void updateMemberInfo(HttpSession session, String memberName, String email) {
        if (memberName != null) {
            session.setAttribute("memberName", memberName);
        }
        if (email != null) {
            session.setAttribute("userEmail", email);
        }
    }
    
    /**
     * 清除 Session
     */
    public void clearSession(HttpSession session) {
        session.removeAttribute("memberProfilePhotoBase64");
        session.removeAttribute("photoVersion");
        session.removeAttribute("memberName");
        session.removeAttribute("userEmail");
        session.removeAttribute("userId");
        session.removeAttribute("loggedInUser");
        session.removeAttribute("userRole");
    }
    
    /**
     * 從 JWT 令牌中獲取用戶 ID
     */
    public Integer getUserIdFromToken(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            try {
                return jwtUtil.extractUserId(token);
            } catch (Exception e) {
                return null;
            }
        }
        
        // 如果沒有令牌，嘗試從 session 中獲取
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userId = session.getAttribute("userId");
            return userId != null ? (Integer) userId : null;
        }
        
        return null;
    }
    
    /**
     * 從 JWT 令牌中獲取用戶角色
     */
    public String getUserRoleFromToken(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            try {
                return jwtUtil.extractUserRole(token);
            } catch (Exception e) {
                return null;
            }
        }
        
        // 如果沒有令牌，嘗試從 session 中獲取
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userRole = session.getAttribute("userRole");
            return userRole != null ? userRole.toString() : null;
        }
        
        return null;
    }
    
    /**
     * 從 JWT 令牌中獲取用戶信息
     */
    public Map<String, Object> getUserInfoFromToken(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        Map<String, Object> userInfo = new HashMap<>();
        
        if (token != null) {
            try {
                userInfo.put("userId", jwtUtil.extractUserId(token));
                userInfo.put("userRole", jwtUtil.extractUserRole(token));
                userInfo.put("username", jwtUtil.extractUsername(token));
                return userInfo;
            } catch (Exception e) {
                // 令牌解析失敗，返回空 Map
                return userInfo;
            }
        }
        
        // 如果沒有令牌，嘗試從 session 中獲取
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userId = session.getAttribute("userId");
            Object userRole = session.getAttribute("userRole");
            Object username = session.getAttribute("userEmail");
            
            if (userId != null) userInfo.put("userId", userId);
            if (userRole != null) userInfo.put("userRole", userRole);
            if (username != null) userInfo.put("username", username);
        }
        
        return userInfo;
    }
    
    /**
     * 檢查用戶是否已登入
     */
    public boolean isUserLoggedIn(HttpServletRequest request) {
        // 先檢查 JWT 令牌
        String token = extractTokenFromRequest(request);
        if (token != null) {
            try {
                return jwtUtil.extractUserId(token) != null;
            } catch (Exception e) {
                // 令牌無效
                return false;
            }
        }
        
        // 如果沒有令牌，檢查 session
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("userId") != null;
    }
    
    /**
     * 從請求中提取 JWT 令牌
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}