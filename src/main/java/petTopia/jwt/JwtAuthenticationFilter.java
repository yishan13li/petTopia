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
import java.util.Collections;
import java.util.List;

import io.jsonwebtoken.Claims;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

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
     * JWT 認證提供者，用於驗證 JWT 令牌的有效性
     */
    @Autowired
    private JwtAuthenticationProvider jwtAuthenticationProvider;

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
        try {
            String jwt = getJwtFromRequest(request);
            
            if (jwt != null && jwtAuthenticationProvider.validateToken(jwt)) {
                // 從 token 中獲取用戶資訊
                Claims claims = jwtUtil.getAllClaimsFromToken(jwt);
                String email = claims.getSubject();
                Integer userId = claims.get("userId", Integer.class);
                String role = claims.get("role", String.class);
                
                // 創建 UserDetails 對象
                UserDetails userDetails = User.builder()
                    .username(email)
                    .password("") // 不需要密碼，因為已經通過 token 驗證
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)))
                    .build();
                
                // 創建認證對象
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
                
                // 設置認證詳情
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                // 將認證信息設置到 SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // 將用戶ID添加到請求屬性中
                request.setAttribute("userId", userId);
            }
        } catch (Exception e) {
            logger.error("無法設置用戶認證", e);
        }
        
        filterChain.doFilter(request, response);
    }

    /**
     * 從請求中提取 JWT 令牌
     * 
     * @param request HTTP 請求
     * @return JWT 令牌，如果存在；否則為 null
     */
    private String getJwtFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring(7);
    }
}
