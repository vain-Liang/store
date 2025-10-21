package online.store.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * JWT 配置类，用于读取 application.yaml 中 jwt 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * JWT 加密密钥 (至少32字节)
     */
    private  String secret;

    /**
     * Token 过期时间 (毫秒)
     */
    private Duration expireTime;

    /**
     * 刷新 Token 过期时间 (毫秒)
     */
    private Duration refreshExpireTime;
}
