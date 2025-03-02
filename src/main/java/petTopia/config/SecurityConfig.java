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
                    // 将用户信息存储到 session
                    session.setAttribute("userId", oauth2User.getAttribute("userId"));
                    session.setAttribute("userRole", oauth2User.getAttribute("userRole"));
                    session.setAttribute("memberName", oauth2User.getAttribute("memberName"));
                    session.setAttribute("email", oauth2User.getAttribute("email"));
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
                .requestMatchers("/", "/member/login/**", "/member/register/**", "/user_static/**", 
                               "/css/**", "/js/**", "/images/**", "/icon/**", "/vendor/**",
                               "/static/**", "/templates/**").permitAll()
                .anyRequest().permitAll()  // 暫時允許所有請求，方便測試
            )
            .formLogin(form -> form
                .loginPage("/member/login")
                .loginProcessingUrl("/member/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/member/login?error=true")
                .permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/member/login")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(oAuth2UserService)
                )
                .successHandler(authenticationSuccessHandler())
                .failureUrl("/member/login?error=true")
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/member/login?logout=true")
                .permitAll()
            );
        
        return http.build();
    }
} 