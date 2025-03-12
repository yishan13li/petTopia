package petTopia.util;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class EcpayUtils {

    // 從 application.properties 中注入 HashKey 和 HashIV
    @Value("${ecpay.hashKey}")
    private String hashKey;

    @Value("${ecpay.hashIv}")
    private String hashIv;

    public String createCheckValue(Map<String, String> params) throws Exception {
        // 步驟 1: 排序參數
        Map<String, String> sortedParams = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();

        // 拼接參數
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }

        
        // 刪除最後一個 "&"
        sb.deleteCharAt(sb.length() - 1);

        // 步驟 2: 加上 HashKey 和 HashIV
        sb.insert(0, "HashKey=" + hashKey + "&");
        sb.append("&HashIV=" + hashIv);

        // 步驟 3: URL 編碼
        String encodedString = encodeString(sb.toString());

        // 步驟 4: 轉小寫
        String lowerCaseString = encodedString.toLowerCase();

        // 步驟 5: SHA256 加密
        String checkMacValue = sha256(lowerCaseString);
        
        // 返回大寫的 CheckMacValue
        return checkMacValue.toUpperCase();
    }

    // URL 編碼方法
    private static String encodeString(String value) throws UnsupportedEncodingException {
        String encoded = URLEncoder.encode(value, "UTF-8");
        encoded = encoded.replaceAll("%21", "!");    // 將 "%21" 轉回 "!"
        encoded = encoded.replaceAll("%2A", "*");    // 將 "%2A" 轉回 "*"
        encoded = encoded.replaceAll("%28", "(");    // 將 "%28" 轉回 "("
        encoded = encoded.replaceAll("%29", ")");    // 將 "%29" 轉回 ")"
        encoded = encoded.replaceAll("%2D", "-");    // 將 "%2D" 轉回 "-"
        encoded = encoded.replaceAll("%5F", "_");    // 將 "%5F" 轉回 "_"
        encoded = encoded.replaceAll("%2E", ".");    // 將 "%2E" 轉回 "."
        return encoded;
    }


    // SHA256 加密方法
    private static String sha256(String value) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(value.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
    
    public boolean isValidCheckValue(Map<String, String> callbackParams, String checkValue) throws Exception {
        // 1. 重新生成 CheckMacValue
        String generatedCheckValue = createCheckValue(callbackParams);
        
        // 2. 比對回傳的 CheckMacValue 和重新生成的 CheckMacValue
        return generatedCheckValue.equals(checkValue);
    }

}
