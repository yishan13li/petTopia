package petTopia.jwt;

// 引入 Servlet 相關類別，用於處理請求與回應
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Spring Security 相關類別
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.io.IOException;

/**
 * JWT 身份驗證過濾器
 * 
 * 主要功能：
 * 1. 攔截所有請求，檢查是否包含有效的 JWT 令牌
 * 2. 驗證 JWT 令牌的有效性
 * 3. 從令牌中提取用戶信息並設置到 Spring Security 上下文中
 * 4. 處理認證失敗的情況
 * 
 * 工作流程：
 * 1. 從請求頭中提取 JWT 令牌
 * 2. 驗證令牌的有效性
 * 3. 從令牌中提取用戶信息
 * 4. 將用戶信息設置到 SecurityContext 中
 * 5. 將請求傳遞給下一個過濾器
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    /**
     * JWT 工具類，用於處理令牌的生成、驗證和解析
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用戶服務，用於加載用戶詳細信息
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 過濾器的核心方法，處理每個請求
     * 
     * @param request HTTP 請求
     * @param response HTTP 響應
     * @param filterChain 過濾器鏈
     * @throws ServletException Servlet 異常
     * @throws IOException IO 異常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 從請求頭中獲取 Authorization 信息
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 檢查 Authorization 頭部是否存在且格式正確
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            // 提取 JWT 令牌（去除 "Bearer " 前綴）
            jwt = authorizationHeader.substring(7);
            try {
                // 從令牌中提取用戶名
                username = jwtUtil.extractUsername(jwt);
            } catch (Exception e) {
                // 記錄令牌解析失敗的錯誤
                logger.error("JWT 令牌解析失敗", e);
            }
        }

        // 如果找到用戶名且當前沒有認證信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // 從數據庫加載用戶詳細信息
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                // 驗證 JWT 令牌是否有效
                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    // 創建認證令牌
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    
                    // 設置認證詳情
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 將認證信息設置到 SecurityContext 中
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    
                    // 將用戶 ID 和角色添加到請求屬性中，方便控制器訪問
                    request.setAttribute("userId", jwtUtil.extractUserId(jwt));
                    request.setAttribute("userRole", jwtUtil.extractUserRole(jwt));
                }
            } catch (UsernameNotFoundException e) {
                // 用戶不存在，記錄警告但不中斷請求處理
                logger.warn("JWT 令牌中的用戶不存在: " + username);
            }
        }

        // 將請求傳遞給下一個過濾器
        filterChain.doFilter(request, response);
    }
}
