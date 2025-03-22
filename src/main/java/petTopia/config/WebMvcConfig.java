package petTopia.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;

/**
 * Spring MVC 配置類，負責:
 * - 靜態資源的映射
 * - 視圖控制（路徑轉發）
 */
@Configuration // 標記為 Spring 配置類
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 配置靜態資源的訪問路徑，使 Spring Boot 可以正確加載靜態文件。
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 將 /static/** 映射到 classpath:/static/
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
        
        // 將 /templates/** 映射到 classpath:/templates/ (一般用於 Thymeleaf 模板)
        registry.addResourceHandler("/templates/**")
                .addResourceLocations("classpath:/templates/");
                
        // 自定義的靜態資源目錄 /user_static/**
        registry.addResourceHandler("/user_static/**")
                .addResourceLocations("classpath:/static/user_static/");
                
        // Swagger UI 的靜態資源映射，確保 Swagger 頁面能夠正常訪問
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
    }
    
    /**
     * 配置視圖控制器，用於處理前端路由。
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 訪問 "http://localhost:8080/" 時，轉發到 index.html
        registry.addViewController("/").setViewName("forward:/index.html");
        
        // 處理單頁應用（SPA）的前端路由，使其不被 Spring 解析，而是轉發到 index.html
        registry.addViewController("/{x:[\\w\\-]+}").setViewName("forward:/index.html");
        
        // 允許多層級的前端路由，確保 Vue/React 等 SPA 框架的深層路由也能正確解析
        registry.addViewController("/{x:^(?!api$).*$}/**/{y:[\\w\\-]+}").setViewName("forward:/index.html");
    }
}