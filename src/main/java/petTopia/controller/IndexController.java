package petTopia.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
public class IndexController {
    
    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
    
    /**
     * 獲取首頁資訊
     */
    @GetMapping({"/", "/index"})
    public ResponseEntity<?> getIndexInfo() {
        logger.info("獲取首頁資訊");
        
        Map<String, Object> indexInfo = new HashMap<>();
        indexInfo.put("appName", "PetTopia");
        indexInfo.put("welcomeMessage", "歡迎來到寵物商城平台");
        indexInfo.put("serverTime", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        indexInfo.put("apiVersion", "1.0");
        
        // 這裡可以添加更多首頁需要的資訊，例如熱門商品、促銷活動等
        
        return ResponseEntity.ok(indexInfo);
    }
    
    /**
     * 獲取系統狀態
     */
    @GetMapping("/status")
    public ResponseEntity<?> getSystemStatus() {
        logger.info("獲取系統狀態");
        
        Map<String, Object> status = new HashMap<>();
        status.put("status", "running");
        status.put("serverTime", LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        status.put("memoryUsage", Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory());
        status.put("availableProcessors", Runtime.getRuntime().availableProcessors());
        
        return ResponseEntity.ok(status);
    }
} 