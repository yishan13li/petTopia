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

@Component
public class JwtUtil {

    // JWT 密鑰，應該從配置文件中讀取
    @Value("${jwt.secret:petTopiaSecretKey12345678901234567890}")
    private String secret;

    // JWT 令牌有效期（毫秒），默認為 24 小時
    @Value("${jwt.expiration:86400000}")
    private long expiration;

    // 生成密鑰
    private Key getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // 從令牌中提取用戶名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 從令牌中提取過期時間
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 從令牌中提取用戶 ID
    public Integer extractUserId(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("userId", Integer.class);
    }

    // 從令牌中提取用戶角色
    public String extractUserRole(String token) {
        final Claims claims = extractAllClaims(token);
        return claims.get("userRole", String.class);
    }

    // 從令牌中提取指定的聲明
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 提取所有聲明
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 檢查令牌是否已過期
    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 為用戶生成令牌
    public String generateToken(String username, Integer userId, String userRole) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("userRole", userRole);
        return createToken(claims, username);
    }

    // 創建令牌
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 驗證令牌
    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }
} 