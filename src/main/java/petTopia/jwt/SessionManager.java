package petTopia.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 會話管理器
 * 
 * 主要功能：
 * 1. 管理用戶會話信息
 * 2. 處理用戶頭像
 * 3. 管理會員信息
 * 4. 處理 JWT 令牌相關操作
 * 
 * 工作流程：
 * 1. 維護用戶會話狀態
 * 2. 處理用戶頭像的存儲和更新
 * 3. 管理用戶基本信息的更新
 * 4. 提供 JWT 令牌信息的提取方法
 */
@Component
public class SessionManager {
    
    /**
     * JWT 工具類，用於處理令牌
     */
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 更新用戶頭像
     * 將頭像數據轉換為 Base64 格式並存儲在會話中
     * 
     * @param session HTTP 會話
     * @param photoData 頭像數據
     */
    public void updateProfilePhoto(HttpSession session, byte[] photoData) {
        if (photoData != null) {
            // 將圖片數據轉換為 Base64 格式
            String photoBase64 = Base64.getEncoder().encodeToString(photoData);
            // 存儲在會話中
            session.setAttribute("memberProfilePhotoBase64", photoBase64);
            // 更新版本號以觸發前端更新
            session.setAttribute("photoVersion", System.currentTimeMillis());
        } else {
            // 如果沒有圖片數據，清除相關屬性
            session.removeAttribute("memberProfilePhotoBase64");
            session.removeAttribute("photoVersion");
        }
    }
    
    /**
     * 更新會員基本信息
     * 
     * @param session HTTP 會話
     * @param memberName 會員名稱
     * @param email 電子郵件
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
     * 清除會話中的所有用戶相關信息
     * 
     * @param session HTTP 會話
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
     * 從請求中提取用戶 ID
     * 優先從 JWT 令牌中獲取，如果沒有則從會話中獲取
     * 
     * @param request HTTP 請求
     * @return 用戶 ID，如果未找到則返回 null
     */
    public Integer getUserIdFromToken(HttpServletRequest request) {
        // 嘗試從 JWT 令牌中獲取
        String token = extractTokenFromRequest(request);
        if (token != null) {
            try {
                return jwtUtil.extractUserId(token);
            } catch (Exception e) {
                // 令牌解析失敗
                return null;
            }
        }
        
        // 從會話中獲取
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userId = session.getAttribute("userId");
            return userId != null ? (Integer) userId : null;
        }
        
        return null;
    }
    
    /**
     * 從請求中提取用戶角色
     * 優先從 JWT 令牌中獲取，如果沒有則從會話中獲取
     * 
     * @param request HTTP 請求
     * @return 用戶角色，如果未找到則返回 null
     */
    public String getUserRoleFromToken(HttpServletRequest request) {
        // 嘗試從 JWT 令牌中獲取
        String token = extractTokenFromRequest(request);
        if (token != null) {
            try {
                return jwtUtil.extractUserRole(token);
            } catch (Exception e) {
                // 令牌解析失敗
                return null;
            }
        }
        
        // 從會話中獲取
        HttpSession session = request.getSession(false);
        if (session != null) {
            Object userRole = session.getAttribute("userRole");
            return userRole != null ? userRole.toString() : null;
        }
        
        return null;
    }
    
    /**
     * 從請求中提取完整的用戶信息
     * 包括用戶 ID、角色和用戶名
     * 
     * @param request HTTP 請求
     * @return 包含用戶信息的 Map
     */
    public Map<String, Object> getUserInfoFromToken(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        Map<String, Object> userInfo = new HashMap<>();
        
        // 嘗試從 JWT 令牌中獲取
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
        
        // 從會話中獲取
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
     * 通過檢查 JWT 令牌或會話中的用戶信息
     * 
     * @param request HTTP 請求
     * @return 如果用戶已登入則返回 true
     */
    public boolean isUserLoggedIn(HttpServletRequest request) {
        // 檢查 JWT 令牌
        String token = extractTokenFromRequest(request);
        if (token != null) {
            try {
                return jwtUtil.extractUserId(token) != null;
            } catch (Exception e) {
                // 令牌無效
                return false;
            }
        }
        
        // 檢查會話
        HttpSession session = request.getSession(false);
        return session != null && session.getAttribute("userId") != null;
    }
    
    /**
     * 從請求中提取 JWT 令牌
     * 
     * @param request HTTP 請求
     * @return JWT 令牌，如果未找到則返回 null
     */
    private String extractTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}