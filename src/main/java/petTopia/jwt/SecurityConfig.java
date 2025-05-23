package petTopia.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import petTopia.model.user.User;
import petTopia.service.user.MemberService;
import petTopia.service.user.MemberLoginService;

import java.util.Arrays;
import java.util.Map;

/**
 * Spring Security 配置類
 * 
 * 主要功能：
 * 1. 配置 JWT 認證
 * 2. 配置 OAuth2 第三方登入
 * 3. 配置 CORS 跨域
 * 4. 配置 HTTP 安全性規則
 * 5. 配置異常處理
 * 
 * 安全特性：
 * 1. 無狀態會話管理
 * 2. CSRF 防護
 * 3. 基於角色的訪問控制
 * 4. 自定義認證和授權處理
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * JWT 工具類，用於處理令牌
     */
    @Autowired
    private JwtUtil jwtUtil;

    /**
     * JWT 認證過濾器
     */
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 會員登入服務，處理會員登入相關操作
     */
    @Autowired
    @Lazy
    private MemberLoginService memberLoginService;

    /**
     * 配置認證管理器
     * 用於處理用戶身份驗證
     * 
     * @param authenticationConfiguration 認證配置
     * @return 認證管理器
     * @throws Exception 配置異常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 配置 HTTP 安全性策略
     * 
     * @param http HTTP 安全配置
     * @return 安全過濾器鏈
     * @throws Exception 配置異常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 配置 CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 禁用 CSRF（因為使用 JWT）
            .csrf(csrf -> csrf.disable())
            // 配置無狀態會話
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置請求授權
            .authorizeHttpRequests(auth -> auth
                // 公開接口
                .requestMatchers(
                    "/api/auth/**", 
                    "/api/oauth2/**", 
                    "/oauth2/authorization/**", 
                    "/oauth2/code/**", 
                    "/oauth2/callback/**",
                    "/api/public/**",
                    "/api/vendor/all",
                    "/api/vendor/category/show",
                    "/shop/**",
                    "/shop/products",
                    "/shop/productDetail",
                    "/api/**",
                    "api/**",
                    "/api/vendor/**",

                    "/api/activity/**",
                    "/manage/**", 

                    "/chat/**", 
                    "/chatRoom/**",
                    "/chatRoomPhoto/**"
                    
                    
                ).permitAll()
                
                // 會員接口 只要新增新的api街口就在這裡添加
                .requestMatchers(
                    "/api/member/**",
                    "/api/vendor/{vendorId}",
                    "/api/vendor/{vendorId}/image",
                    "/api/vendor/category/{categoryId}",
                    "/api/vendor/{vendorId}/review",
                    "/api/vendor/review/{reviewId}",
                    "/api/vendor/review/{reviewId}/photo",
                    "/activity/all",
                    "/activity/{activityId}",
                    "/activity/{activityId}/review",
                    "/shop/orderHistory",
                    "/shop/checkout",
                    "/shop/orders",
                    "/vendor/**",
                    "/api/vendor/**",
                    "/api/activity/**"
                ).hasRole("MEMBER")
                
                // 商家接口 只要新增新的api街口就在這裡添加
                .requestMatchers(
                    "/api/vendor/**",
                    "/activity/**",
                    "/api/vendor/{vendorId}/review/add",
                    "/api/vendor/{vendorId}/review/star/add",
                    "/api/vendor/review/{reviewId}/rewrite",
                    "/api/vendor/review/{reviewId}/delete",
                    "/vendor/admin/**"
                ).hasAnyRole("MEMBER", "VENDOR")
                
                // 管理員接口 只要新增新的api街口就在這裡添加
                .requestMatchers(
                    "/api/admin/**",
                    "/api/admin/dashboard",
                    "/api/admin/users/**",
                    "/api/admin/members",
                    "/api/admin/vendors"
                ).hasRole("ADMIN")
                
                // 其他請求需要認證
                .anyRequest().authenticated()
            )
            // 配置 OAuth2 登入
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    try {
                        // 獲取 OAuth2 認證信息
                        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                        
                        // 提取用戶信息
                        String email = oauth2User.getAttribute("email");
                        String name = oauth2User.getAttribute("name");
                        String provider = oauthToken.getAuthorizedClientRegistrationId().toUpperCase();
                        
                        // 驗證郵箱
                        if (email == null || email.isEmpty()) {
                            response.sendRedirect("http://localhost:5173/login?error=true&message=" + 
                                java.net.URLEncoder.encode("未獲取到電子郵件，無法完成登入", "UTF-8"));
                            return;
                        }
                        
                        // 查找用戶
                        User user = memberLoginService.findByEmail(email);
                        Integer userId = (user != null) ? user.getId() : null;
                        String role = (user != null) ? user.getUserRole().toString() : "MEMBER";
                        
                        // 驗證用戶存在
                        if (user == null) {
                            response.sendRedirect("http://localhost:5173/login?error=true&message=" + 
                                java.net.URLEncoder.encode("用戶不存在，請聯繫管理員", "UTF-8"));
                            return;
                        }
                        
                        // 生成 JWT 令牌
                        String token = jwtUtil.generateToken(email, userId, role);
                        response.sendRedirect("http://localhost:5173/login?token=" + token + 
                            "&userId=" + userId + "&email=" + email + "&role=" + role);
                    } catch (Exception e) {
                        response.sendRedirect("http://localhost:5173/login?error=true&message=" + 
                            java.net.URLEncoder.encode("登入過程中發生錯誤: " + e.getMessage(), "UTF-8"));
                    }
                })
            )
            // 添加 JWT 過濾器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            // 配置異常處理
            .exceptionHandling(exception -> exception
                // 處理未認證異常
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(401);
                    response.getWriter().write("{\"error\":\"未登入或登入已過期\"}");
                })
                // 處理未授權異常
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(403);
                    response.getWriter().write("{\"error\":\"沒有權限訪問此資源\"}");
                })
            );
        return http.build();
    }

    /**
     * 配置 CORS 設定
     * 允許跨域請求
     * 
     * @return CORS 配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 允許的來源
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000","http://localhost:5174"));
        // 允許的 HTTP 方法
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // 允許的請求頭
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        // 允許發送認證信息
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
