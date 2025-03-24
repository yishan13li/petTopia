package petTopia.controller.user;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;

@RestController
@RequestMapping("/api")
public class RoutingController {
    
    private static final Logger logger = LoggerFactory.getLogger(RoutingController.class);
    
    /**
     * 獲取應用程式的基本資訊
     */
    @GetMapping("/app-info")
    public ResponseEntity<?> getAppInfo() {
        logger.info("獲取應用程式基本資訊");
        
        Map<String, Object> appInfo = new HashMap<>();
        appInfo.put("name", "PetTopia");
        appInfo.put("version", "1.0.0");
        appInfo.put("description", "寵物商城平台");
        
        return ResponseEntity.ok(appInfo);
    }
    
    /**
     * 獲取可用的頁面路由
     */
    @GetMapping("/routes")
    public ResponseEntity<?> getRoutes() {
        logger.info("獲取可用的頁面路由");
        
        List<Map<String, Object>> routes = Arrays.asList(
            Map.of(
                "path", "/",
                "name", "首頁",
                "requiresAuth", false
            ),
            Map.of(
                "path", "/login",
                "name", "會員登入",
                "requiresAuth", false
            ),
            Map.of(
                "path", "/register",
                "name", "會員註冊",
                "requiresAuth", false
            ),
            Map.of(
                "path", "/vendor/login",
                "name", "商家登入",
                "requiresAuth", false
            ),
            Map.of(
                "path", "/vendor/register",
                "name", "商家註冊",
                "requiresAuth", false
            ),
            Map.of(
                "path", "/products",
                "name", "商品列表",
                "requiresAuth", false
            ),
            Map.of(
                "path", "/profile",
                "name", "會員資料",
                "requiresAuth", true,
                "role", "MEMBER"
            ),
            Map.of(
                "path", "/vendor/admin",
                "name", "商家管理",
                "requiresAuth", true,
                "role", "VENDOR"
            )
        );
        
        return ResponseEntity.ok(Map.of("routes", routes));
    }
    
    /**
     * 健康檢查端點
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        logger.info("執行健康檢查");
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "timestamp", System.currentTimeMillis()
        ));
    }
}