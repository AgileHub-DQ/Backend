package dynamicquad.agilehub.global.auth.util;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RedisUtil {

    private final StringRedisTemplate redisTemplate;

    @Transactional
    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Transactional
    public void setValueWithExpire(String key, String value, long expire) {
        redisTemplate.opsForValue().set(key, value, expire, TimeUnit.MILLISECONDS);
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Transactional
    public void deleteValue(String key) {
        redisTemplate.delete(key);
    }

}
