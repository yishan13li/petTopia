package petTopia.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Base64;
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
     * 使用 Base64 編碼的隨機字符串
     */
    private static final String SECRET = "bXlTdXBlclNlY3VyZUtleUZvckpXVFNpZ25pbmdBbmRWZXJpZmljYXRpb25PZk5vbmV4cGlyaW5nVG9rZW5zRm9yUGV0VG9waWE=";

    /**
     * JWT 令牌有效期（24小時）
     */
    private static final long EXPIRATION = 86400000L; // 24 hours in milliseconds

    /**
     * 生成用於簽名的密鑰
     * 使用 HMAC-SHA 算法
     * @return 用於簽名的 Key 對象
     */
    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET);
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
        Claims claims = extractAllClaims(token);
        return claims.get("userId", Integer.class);
    }

    /**
     * 從令牌中提取用戶角色
     * @param token JWT 令牌
     * @return 用戶角色
     */
    public String extractUserRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
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
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * 生成 JWT 令牌
     * @param email 用戶郵箱
     * @param userId 用戶ID
     * @param role 用戶角色
     * @return JWT 令牌
     */
    public String generateToken(String email, Integer userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("role", role);
        return createToken(claims, email);
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
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 驗證 JWT 令牌
     * @param token JWT 令牌
     * @param userDetails 用戶詳情
     * @return 如果令牌有效返回 true
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * 驗證 JWT 令牌
     * @param token JWT 令牌
     * @param username 用戶名
     * @return 如果令牌有效返回 true
     */
    public Boolean validateToken(String token, String username) {
        return (username.equals(extractUsername(token)) && !isTokenExpired(token));
    }

    /**
     * 從令牌中提取用戶資訊
     * @param token JWT 令牌
     * @return 用戶資訊
     */
    public Map<String, Object> extractUserInfo(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("username", claims.getSubject());
            userInfo.put("userId", claims.get("userId", Integer.class));
            userInfo.put("userRole", claims.get("role", String.class));
            return userInfo;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    /**
     * 從令牌中獲取所有聲明
     * @param token JWT 令牌
     * @return Claims 對象
     */
    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(getSigningKey())
            .build()
            .parseClaimsJws(token)
            .getBody();
    }

    /**
     * 驗證 JWT 令牌
     * @param token JWT 令牌
     * @return 如果令牌有效返回 true，否則返回 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
} 