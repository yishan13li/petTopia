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

import petTopia.model.user.Users;
import petTopia.service.user.MemberService;
import petTopia.service.user.MemberLoginService;

import java.util.Arrays;
import java.util.Map;

/**
 * Spring Security 配置類，負責:
 * - JWT 驗證
 * - OAuth2 登入
 * - CORS 設定
 * - HTTP 安全性規則
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    @Lazy
    private MemberLoginService memberLoginService;

    /**
     * 配置 AuthenticationManager，處理用戶身份驗證。
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 配置 HTTP 安全性策略。
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**", "/api/oauth2/**", "/oauth2/authorization/**", "/oauth2/code/**", "/api/public/**").permitAll()
                .requestMatchers("/api/member/**").hasRole("MEMBER")
                .requestMatchers("/api/vendor-admin/**").hasRole("VENDOR")
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .successHandler((request, response, authentication) -> {
                    try {
                        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
                        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                        String email = oauth2User.getAttribute("email");
                        String name = oauth2User.getAttribute("name");
                        String provider = oauthToken.getAuthorizedClientRegistrationId().toUpperCase();
                        
                        if (email == null || email.isEmpty()) {
                            response.sendRedirect("http://localhost:5173/login?error=true&message=" + 
                                java.net.URLEncoder.encode("未獲取到電子郵件，無法完成登入", "UTF-8"));
                            return;
                        }
                        
                        Users user = memberLoginService.findByEmail(email);
                        Integer userId = (user != null) ? user.getId() : null;
                        String role = (user != null) ? user.getUserRole().toString() : "MEMBER";
                        
                        if (user == null) {
                            response.sendRedirect("http://localhost:5173/login?error=true&message=" + 
                                java.net.URLEncoder.encode("用戶不存在，請聯繫管理員", "UTF-8"));
                            return;
                        }
                        
                        String token = jwtUtil.generateToken(email, userId, role);
                        response.sendRedirect("http://localhost:5173/login?token=" + token + "&userId=" + userId + "&email=" + email + "&role=" + role);
                    } catch (Exception e) {
                        response.sendRedirect("http://localhost:5173/login?error=true&message=" + java.net.URLEncoder.encode("登入過程中發生錯誤: " + e.getMessage(), "UTF-8"));
                    }
                })
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(401);
                    response.getWriter().write("{\"error\":\"未登入或登入已過期\"}");
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType("application/json;charset=UTF-8");
                    response.setStatus(403);
                    response.getWriter().write("{\"error\":\"沒有權限訪問此資源\"}");
                })
            );
        return http.build();
    }

    /**
     * 配置 CORS 設定，允許跨來源請求。
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
