package petTopia.util;

import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.TreeMap;
import java.io.UnsupportedEncodingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;

@Component
public class EcpayUtils {

    @Value("${ecpay.hashKey}")
    private String hashKey;

    @Value("${ecpay.hashIv}")
    private String hashIv;

    public String createCheckValue(Map<String, String> params) throws Exception {
        Map<String, String> sortedParams = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            // 排除 PaymentStatus 和 CheckMacValue
            if ("CheckMacValue".equals(entry.getKey())) {
                continue;
            }
            
            // **如果是 TradeDesc 或 ItemName，則先進行 URL encoding**
            String value = entry.getValue();
            if ("TradeDesc".equals(entry.getKey()) || "ItemName".equals(entry.getKey())) {
                value = encodeString(value);
            }
            
            sb.append(entry.getKey()).append("=").append(value).append("&");
        }

        sb.deleteCharAt(sb.length() - 1);
        sb.insert(0, "HashKey=" + hashKey + "&");
        sb.append("&HashIV=" + hashIv);
        System.out.println("原始字串: " + sb);

        String encodedString = encodeString(sb.toString());
        System.out.println("URL 編碼後: " + encodedString);

        String lowerCaseString = encodedString.toLowerCase();
        String checkMacValue = sha256(lowerCaseString);
        
        System.out.println("CheckMacValue: " + checkMacValue.toUpperCase());
        return checkMacValue.toUpperCase();
    }


    private static String encodeString(String value) throws UnsupportedEncodingException {
        String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());

        encoded = encoded.replaceAll("\\+", "%20")  // 修正 `+` 為 `%20`
                .replaceAll("%21", "!")
                .replaceAll("%2A", "*")
                .replaceAll("%28", "(")
                .replaceAll("%29", ")")
                .replaceAll("%2D", "-")
                .replaceAll("%5F", "_")
                .replaceAll("%2E", ".");

        return encoded;
    }

    private static String sha256(String value) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(value.getBytes(StandardCharsets.UTF_8)); // **明確指定 UTF-8**

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

    public boolean isValidCheckValue(Map<String, String> callbackParams) throws Exception {
        // 1. 取出 ECPay 回傳的 CheckMacValue
        String ecpayCheckMacValue = callbackParams.get("CheckMacValue");

        // 2. 重新計算 CheckMacValue
        String generatedCheckMacValue = createCheckValue(callbackParams);

        // 3. 比對兩者是否相同
        return generatedCheckMacValue.equals(ecpayCheckMacValue);
    }

}
