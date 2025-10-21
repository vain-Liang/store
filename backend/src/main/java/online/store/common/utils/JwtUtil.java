package online.store.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import online.store.config.JwtConfig;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类, 生成、解析、校验 Token
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    /**
     * JWT 配置
     * -- GETTER --
     * 获取 JWT 配置
     */
    @Getter
    private final JwtConfig jwtConfig;

    /**
     * JWT 加密密钥
     */
    private SecretKey key;

    /**
     * 初始化加密密钥
     */
    @PostConstruct
    public void init() {
        key = Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成 JWT Token
     * @param username 用户名
     * @param role 用户角色
     * @return 生成的已签名JWT
     */
    public String generateAccessToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        Instant now = Instant.now();
        Instant expiry = now.plus(jwtConfig.getExpireTime());
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(key)
                .compact();
    }

    /**
     * 生成 RefreshToken
     * @param username 用户名
     * @return 生成的已签名RefreshJWT
     */
    public String generateRefreshToken(String username) {
        Instant now = Instant.now();
        Instant refreshExpiry = now.plus(jwtConfig.getRefreshExpireTime());
        return Jwts.builder()
                .subject(username) // 刷新Token不包含角色信息, 只包含必要信息
                .issuedAt(Date.from(now))
                .expiration(Date.from(refreshExpiry))
                .signWith(key)
                .compact();
    }

    /**
     * 解析并验证 JWT
     * @param token 登录用户JWT
     * @return 解析签名后的 Claims (包含 payload 数据)
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证 JWT Token 是否过期
     * @param token JWT Token
     * @return true过期, false未过期
     */
    public boolean isExpired(String token) {
        try {
            Instant expiration = parseToken(token).getExpiration().toInstant();
            return expiration.isBefore(Instant.now());
        } catch (Exception e) {
            return true;
        }
    }
}
