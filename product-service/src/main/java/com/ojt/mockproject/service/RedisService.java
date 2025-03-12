package com.ojt.mockproject.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String BLACKLIST_PREFIX = "blacklist:";
    private static final String DATA_PREFIX = "data:";

    public void addToBlacklist(String ip, long expirationTimeInMillis) {
        redisTemplate.opsForValue().set(BLACKLIST_PREFIX + ip, "invalid", expirationTimeInMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isIpBlacklisted(String ip) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(BLACKLIST_PREFIX + ip));
    }

    // Create or update a key-value pair
    public void setValue(String key, String value, long expirationTimeInMillis) {
        redisTemplate.opsForValue().set(DATA_PREFIX + key, value, expirationTimeInMillis, TimeUnit.MILLISECONDS);
    }

    // Read the value of a key
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(DATA_PREFIX + key);
    }

    public List<String> getAllBlacklistedIps() {
        return redisTemplate.keys(BLACKLIST_PREFIX + "*").stream()
                .map(key -> key.replace(BLACKLIST_PREFIX, ""))
                .collect(Collectors.toList());
    }

    // Delete a key
    public void deleteKey(String key) {
        redisTemplate.delete(DATA_PREFIX + key);
    }
}