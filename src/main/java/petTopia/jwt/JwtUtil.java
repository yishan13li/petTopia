package petTopia.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 工具類
 * 負責處理 JWT 令牌的生成、驗證和解析
 * 主要功能：
 * 1. 生成 JWT 令牌
 * 2. 驗證 JWT 令牌
 * 3. 從令牌中提取用戶信息
 * 4. 檢查令牌是否過期
 */
@Component
public class JwtUtil {

    /**
     * JWT 密鑰
     * 從配置文件中讀取，如果未配置則使用默認值
     * 建議在生產環境中使用更長的密鑰
     */
    @Value("${jwt.secret:petTopiaSecretKey12345678901234567890}")
    private String secret;

    /**
     * JWT 令牌有效期
     * 從配置文件中讀取，默認為 24 小時（86400000 毫秒）
     */
    @Value("${jwt.expiration:86400000}")
    private long expiration;

    /**
     * 生成用於簽名的密鑰
     * 使用 HMAC-SHA 算法
     * @return 用於簽名的 Key 對象
     */
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 從令牌中提取用戶名
     * @param token JWT 令牌
     * @return 用戶名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * 從令牌中提取過期時間
     * @param token JWT 令牌
     * @return 過期時間
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * 從令牌中提取用戶 ID
     * @param token JWT 令牌
     * @return 用戶 ID
     */
    public Integer extractUserId(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("userId", Integer.class);
    }

    /**
     * 從令牌中提取用戶角色
     * @param token JWT 令牌
     * @return 用戶角色
     */
    public String extractUserRole(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("userRole", String.class);
    }

    /**
     * 從令牌中提取指定的聲明
     * @param token JWT 令牌
     * @param claimsResolver 用於提取特定聲明的函數
     * @return 提取的聲明值
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 提取令牌中的所有聲明
     * @param token JWT 令牌
     * @return 所有聲明
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 檢查令牌是否已過期
     * @param token JWT 令牌
     * @return 如果令牌已過期返回 true
     */
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 為用戶生成新的 JWT 令牌
     * @param username 用戶名
     * @param userId 用戶 ID
     * @param userRole 用戶角色
     * @return 生成的 JWT 令牌
     */
    public String generateToken(String username, Integer userId, String userRole) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userRole", userRole);
        return createToken(claims, username);
    }

    /**
     * 創建 JWT 令牌
     * @param claims 要包含在令牌中的聲明
     * @param subject 令牌主題（通常是用戶名）
     * @return 生成的 JWT 令牌
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 驗證 JWT 令牌
     * @param token JWT 令牌
     * @param username 用戶名
     * @return 如果令牌有效返回 true
     */
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
} 