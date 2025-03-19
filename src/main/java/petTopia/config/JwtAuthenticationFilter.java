package petTopia.config;

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

// 自訂工具類，用於 JWT 解析與驗證
import petTopia.util.JwtUtil;

import java.io.IOException;

/**
 * JWT 身份驗證過濾器，用於攔截請求並驗證 JWT 是否有效。
 * 若 JWT 有效，則將用戶身份存入 Spring Security 上下文。
 */
@Component // 標記為 Spring 組件，讓 Spring 自動管理
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil; // JWT 工具類，負責解析與驗證 JWT

    @Autowired
    private UserDetailsService userDetailsService; // 用戶服務，用於加載用戶詳細信息

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        final String authorizationHeader = request.getHeader("Authorization"); // 從請求標頭中獲取 JWT

        String username = null;
        String jwt = null;

        // 檢查 Authorization 頭部是否存在且以 "Bearer " 開頭
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // 提取 JWT（去除 "Bearer " 前綴）
            try {
                username = jwtUtil.extractUsername(jwt); // 從 JWT 解析出用戶名
            } catch (Exception e) {
                logger.error("JWT 令牌解析失敗", e);
            }
        }

        // 如果找到了用戶名且當前沒有認證信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); // 從資料庫加載用戶信息

                // 驗證 JWT 是否有效
                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    // 建立 Spring Security 的身份驗證對象
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    
                    // 將身份驗證信息存入 SecurityContextHolder，讓 Spring Security 識別當前用戶
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    
                    // 將用戶 ID 和角色添加到請求屬性中，方便控制器訪問
                    request.setAttribute("userId", jwtUtil.extractUserId(jwt));
                    request.setAttribute("userRole", jwtUtil.extractUserRole(jwt));
                }
            } catch (UsernameNotFoundException e) {
                // 用戶不存在，記錄錯誤但不中斷請求處理
                logger.warn("JWT 令牌中的用戶不存在: " + username);
            }
        }

        // 讓請求繼續進入下一個過濾器或控制器
        filterChain.doFilter(request, response);
    }
}
