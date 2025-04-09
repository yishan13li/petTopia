package petTopia.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class JwtAuthenticationProvider {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 驗證 JWT 令牌
     * @param token JWT 令牌
     * @return 如果令牌有效返回 true，否則返回 false
     */
    public boolean validateToken(String token) {
        try {
            return jwtUtil.validateToken(token);
        } catch (Exception e) {
            return false;
        }
    }
} 