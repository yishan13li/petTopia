package petTopia.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * API 響應工具類，用於統一 API 響應格式
 */
public class ApiResponse {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    
    /**
     * 創建成功響應
     * @param message 成功消息
     * @return 包含成功信息的 Map
     */
    public static Map<String, Object> success(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        response.put("timestamp", LocalDateTime.now().format(FORMATTER));
        return response;
    }
    
    /**
     * 創建成功響應，帶有數據
     * @param message 成功消息
     * @param data 響應數據
     * @return 包含成功信息和數據的 Map
     */
    public static Map<String, Object> success(String message, Object data) {
        Map<String, Object> response = success(message);
        response.put("data", data);
        return response;
    }
    
    /**
     * 創建成功響應，帶有數據和分頁信息
     * @param message 成功消息
     * @param data 響應數據
     * @param page 當前頁碼
     * @param size 每頁大小
     * @param total 總記錄數
     * @return 包含成功信息、數據和分頁信息的 Map
     */
    public static Map<String, Object> success(String message, Object data, int page, int size, long total) {
        Map<String, Object> response = success(message, data);
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("page", page);
        pagination.put("size", size);
        pagination.put("total", total);
        pagination.put("totalPages", (int) Math.ceil((double) total / size));
        response.put("pagination", pagination);
        return response;
    }
    
    /**
     * 創建錯誤響應
     * @param message 錯誤消息
     * @return 包含錯誤信息的 Map
     */
    public static Map<String, Object> error(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        response.put("timestamp", LocalDateTime.now().format(FORMATTER));
        return response;
    }
    
    /**
     * 創建錯誤響應，帶有錯誤代碼
     * @param message 錯誤消息
     * @param errorCode 錯誤代碼
     * @return 包含錯誤信息和錯誤代碼的 Map
     */
    public static Map<String, Object> error(String message, String errorCode) {
        Map<String, Object> response = error(message);
        response.put("errorCode", errorCode);
        return response;
    }
    
    /**
     * 創建錯誤響應，帶有詳細錯誤信息
     * @param message 錯誤消息
     * @param details 詳細錯誤信息
     * @return 包含錯誤信息和詳細錯誤信息的 Map
     */
    public static Map<String, Object> error(String message, Map<String, Object> details) {
        Map<String, Object> response = error(message);
        response.put("details", details);
        return response;
    }
} 