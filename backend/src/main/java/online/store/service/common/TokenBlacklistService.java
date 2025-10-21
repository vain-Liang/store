package online.store.service.common;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Token 黑名单服务
 * <p>用于将 JWT Token 加入黑名单, 以及检查 Token 是否在黑名单中</p>
 * <p>依赖 Redis 存储黑名单数据</p>
 */
@Service
public class TokenBlacklistService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    /**
     * 将 Token 加入黑名单中
     * @param token 要加入黑名单的 JWT 令牌
     * @param expirationMillis 该 Token 的剩余有效时间（毫秒），用于设置 Redis 键的过期时间
     */
    public void add(String token, long expirationMillis) {
        // 确保至少有1秒的存储时间，避免负数或0
        if (expirationMillis > 0) {
            String key = BLACKLIST_PREFIX + token;
            stringRedisTemplate.opsForValue().set(key, "1", expirationMillis, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * 检查 Token 是否在黑名单中
     * @param token 要检查的 JWT Token
     * @return 如果 Token 在黑名单中则返回 true, 否则返回 false
     */
    public boolean isBlacklisted(String token) {
        String key = BLACKLIST_PREFIX + token;
        return stringRedisTemplate.hasKey(key);
    }
}
