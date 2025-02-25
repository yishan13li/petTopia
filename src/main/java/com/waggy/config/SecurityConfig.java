package com.waggy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/vendor_register",              // 註冊頁面
                    "/vendor/vendor_register/**",    // 註冊相關的路徑
                    "/api/vendor/send-verification", // 發送驗證碼的API
                    "/api/vendor/verify-code",       // 驗證碼驗證的API
                    "/user_static/**",               // 靜態資源
                    "/vendor_login"                  // 登入頁面
                ).permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/vendor_login")
                .permitAll()
            )
            .csrf(csrf -> csrf.disable());  // 在開發階段可以先禁用CSRF

        return http.build();
    }
} 