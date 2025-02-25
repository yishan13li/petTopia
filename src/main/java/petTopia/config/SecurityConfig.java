package petTopia.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
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
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/icon/**").permitAll()
                    .anyRequest().permitAll();  // 暫時允許所有請求，方便測試
            })
            .formLogin(form -> {
                form.loginPage("/vendor_login")
                    .permitAll();
            });
        
        return http.build();
    }
} 