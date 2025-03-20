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
        
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            final String email = jwtUtil.extractUsername(jwt);

            if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
                
                if (jwtUtil.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("JWT 認證失敗", e);
        }

        filterChain.doFilter(request, response);
    }
}
