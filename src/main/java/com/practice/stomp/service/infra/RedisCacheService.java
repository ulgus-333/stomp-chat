package com.practice.stomp.service.infra;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
public class RedisCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final ValueOperations<String, Object> valueOperations;

    public RedisCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }

    public <T> Optional<T> get(String key, Class<T> clazz) {
        assertKeyNotBlank(key);

        try {
            return Optional.ofNullable(valueOperations.get(key))
                    .map(clazz::cast);
        } catch (Exception e) {
            log.error("Redis exception", e);
            return Optional.empty();
        }
    }

    public boolean set(String key, Object value, Duration expire) {
        assertKeyNotBlank(key);
        assertValueNotNull(value);

        try {
            valueOperations.set(key, value, expire);
            return true;
        } catch (Exception e) {
            log.error("Redis exception", e);
            return false;
        }
    }

    public boolean delete(String key) {
        assertKeyNotBlank(key);
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }

    private void assertKeyNotBlank(String key) {
        Assert.hasLength(key, "key must not be empty");
    }

    private void assertValueNotNull(Object value) {
        Assert.notNull(value, "value must not be null");
    }
}
