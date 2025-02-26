package petTopia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> {
                auth
                    .requestMatchers("/user_static/**").permitAll()
                    .requestMatchers("/vendor_login").permitAll()
                    .requestMatchers("/vendor_register").permitAll()
                    .requestMatchers("/vendor/**").permitAll()
                    .requestMatchers("/member/**").permitAll()
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/icon/**").permitAll()
                    .anyRequest().permitAll();  // 暫時允許所有請求，方便測試
            })
            .formLogin(form -> {
                form.loginPage("/member_login")
                    .loginProcessingUrl("/member/login")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/member_login?error=true")
                    .permitAll();
            })
            .formLogin(form -> {
                form.loginPage("/vendor_login")
                    .loginProcessingUrl("/vendor/login")
                    .defaultSuccessUrl("/", true)  // vendor 登入成功後也跳轉至首頁
                    .failureUrl("/vendor_login?error=true")
                    .permitAll();
            });
        
        return http.build();
    }
} 