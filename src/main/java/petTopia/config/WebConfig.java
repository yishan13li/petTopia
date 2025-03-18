// package petTopia.config;

// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// @Configuration
// public class WebConfig implements WebMvcConfigurer {

//     @Override
//     public void addCorsMappings(CorsRegistry registry) {
//         registry.addMapping("/api/**")
//                 .allowedOrigins(
//                     "http://localhost:5173",  // Vue.js 開發伺服器的默認端口
//                     "http://localhost:8080",  // 另一個常用的開發端口
//                     "http://localhost:3000"   // React 開發伺服器的默認端口
//                 )
//                 .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
//                 .allowedHeaders("*")
//                 .exposedHeaders("Authorization")
//                 .allowCredentials(true)
//                 .maxAge(3600); // 1小時的預檢請求緩存
//     }
// } 