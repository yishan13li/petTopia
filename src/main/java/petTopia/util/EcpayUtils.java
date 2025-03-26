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

//    @Value("${ecpay.hashKey}")
//    private String hashKey;
//
//    @Value("${ecpay.hashIv}")
//    private String hashIv;

    public String createCheckValue(Map<String, String> params) throws Exception {
        Map<String, String> sortedParams = new TreeMap<>(params);
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if ("CheckMacValue".equals(entry.getKey())) {
                continue;
            }
            
            String value = entry.getValue();
            
            sb.append(entry.getKey()).append("=").append(value).append("&");
        }

        sb.deleteCharAt(sb.length() - 1);
        
        sb.insert(0, "HashKey=" + "pwFHCqoQZGmho4w6" + "&");
        sb.append("&HashIV=" + "EkRm7iFT261dpevs");

        String encodedString = encodeString(sb.toString());
        
        String lowerCaseString = encodedString.toLowerCase();
        
        String checkMacValue = sha256(lowerCaseString);
        
        System.out.println(checkMacValue.toUpperCase());
        return checkMacValue.toUpperCase();
    }


    private static String encodeString(String value) throws UnsupportedEncodingException {
        String encoded = URLEncoder.encode(value, StandardCharsets.UTF_8.toString());

        // 修正常見的 URL 編碼錯誤，讓它符合 ECPay 的規範
        encoded = encoded.replaceAll("%21", "!")   // `!`
                         .replaceAll("%2a", "*")   // `*`
                         .replaceAll("%28", "(")   // `(`
                         .replaceAll("%29", ")")   // `)`
                         .replaceAll("%2d", "-")   // `-`
                         .replaceAll("%5f", "_")   // `_`
                         .replaceAll("%2e", ".")   // `.`
                         .replaceAll("%7e", "~")   // `~`
                         .replaceAll("%40", "@")   // `@`
                         .replaceAll("%23", "#")   // `#`
                         .replaceAll("%24", "$")   // `$`
                         .replaceAll("%25", "%")   // `%`
                         .replaceAll("%5e", "^")   // `^`
                         .replaceAll("%26", "&")   // `&`
                         .replaceAll("%3d", "=")   // `=`
                         .replaceAll("%2b", "+")   // `+`
                         .replaceAll("%3b", ";")   // `;`
                         .replaceAll("%3f", "?")   // `?`
                         .replaceAll("%2f", "/")   // `/`
                         .replaceAll("%5c", "\\")  // `\`
                         .replaceAll("%3e", ">")   // `>`
                         .replaceAll("%3c", "<")   // `<`
                         .replaceAll("%60", "`")   // `` ` ``
                         .replaceAll("%5b", "[")   // `[`
                         .replaceAll("%5d", "]")   // `]`
                         .replaceAll("%7b", "{")   // `{`
                         .replaceAll("%7d", "}")   // `}`
                         .replaceAll("%3a", ":")   // `:`
                         .replaceAll("%27", "'")   // `'`
                         .replaceAll("%22", "\"")  // `"`
                         .replaceAll("%2c", ",")   // `,`
                         .replaceAll("%7c", "|");  // `|`

        encoded = encoded.replace("&", "%26");
        encoded = encoded.replace("#", "%23");
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
