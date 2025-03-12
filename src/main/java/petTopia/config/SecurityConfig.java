package petTopia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import petTopia.service.user.OAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import petTopia.model.user.Users;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Autowired
    private OAuth2UserService oAuth2UserService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                    Authentication authentication) throws IOException, ServletException {
                HttpSession session = request.getSession();
                
                if (authentication.getPrincipal() instanceof OAuth2User) {
                    OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();
                    
                    // 檢查是否需要綁定本地帳號
                    Boolean localAccountExists = oauth2User.getAttribute("localAccountExists");
                    if (localAccountExists != null && localAccountExists) {
                        // 將綁定資訊存入 session
                        session.setAttribute("bindingRequired", true);
                        session.setAttribute("localUserId", oauth2User.getAttribute("localUserId"));
                        session.setAttribute("localUserRole", oauth2User.getAttribute("localUserRole"));
                        session.setAttribute("oauthEmail", oauth2User.getAttribute("email"));
                        session.setAttribute("oauthProvider", oauth2User.getAttribute("provider"));
                        
                        // 重定向到綁定確認頁面
                        response.sendRedirect("/oauth2/bind-confirm");
                        return;
                    }
                    
                    // 正常的登入流程
                    Integer userId = oauth2User.getAttribute("userId");
                    session.setAttribute("userId", userId);
                    session.setAttribute("userRole", oauth2User.getAttribute("userRole"));
                    
                    String displayName = oauth2User.getAttribute("memberName");
                    String email = oauth2User.getAttribute("email");
                    if (displayName == null || displayName.trim().isEmpty()) {
                        displayName = email;
                    }
                    
                    Users user = new Users();
                    user.setId(userId);
                    user.setEmail(email);
                    user.setUserRole(Users.UserRole.MEMBER);
                    user.setEmailVerified(true);
                    user.setProvider(Users.Provider.valueOf(oauth2User.getAttribute("provider").toString()));
                    
                    session.setAttribute("loggedInUser", user);
                    session.setAttribute("memberName", displayName);
                    session.setAttribute("email", email);
                    session.setAttribute("userEmail", email);
                    session.setAttribute("provider", oauth2User.getAttribute("provider"));
                }
                
                response.sendRedirect("/");
            }
        };
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())  // 暫時禁用 CSRF
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login/**", "/register/**", "/user_static/**", 
                               "/css/**", "/js/**", "/images/**", "/icon/**", "/vendor/**",
                               "/static/**", "/templates/**", "/member/**", "/api/**",
                               "/oauth2/bind-confirm").permitAll()
                .anyRequest().permitAll()  // 暫時允許所有請求，方便測試
            )
            // 會員登入配置
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/api/member/login")  // 修改為實際的登入處理URL
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            // OAuth2登入配置
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oAuth2UserService)
                )
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/login?error=true")
            )
            // 登出配置
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );
        
        return http.build();
    }
} 