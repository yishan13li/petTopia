package petTopia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")  // 所有 API 路徑都允許跨域
                .allowedOrigins("http://localhost:5173") // 允許來自這個來源的請求
                .allowedMethods("GET", "POST", "PUT", "DELETE")  // 設定允許的 HTTP 方法
                .allowedHeaders("*")  // 允許的請求頭
                .allowCredentials(true); // 允許攜帶 Cookies
    }
}
