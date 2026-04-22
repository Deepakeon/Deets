package org.example.deets.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final JedisPool redis;
    private final ObjectMapper objectMapper;

    private final long ttlSeconds = 3600;

    public void setValue(String key, String value) {
        try (Jedis jedis = redis.getResource()) {
            jedis.set(key, value);
        }
    }

    public <T> Optional<T> getOrCache(String key, Class<T> classType, Supplier<Optional<T>> dbLoader){
        String cachedResource = getValue(key);
        if(cachedResource != null && !cachedResource.isEmpty()){
            return Optional.of(objectMapper.readValue(cachedResource, classType));
        }

        return dbLoader.get()
                .map(resource -> {
                    setValueWithExpiry(key, objectMapper.writeValueAsString(resource), ttlSeconds);
                   return resource;
                });
    }

    public void setValueWithExpiry(String key, String value, long seconds) {
        try (Jedis jedis = redis.getResource()) {
            jedis.setex(key, seconds, value);
        }
    }

    public String getValue(String key) {
        try (Jedis jedis = redis.getResource()) {
            return jedis.get(key);
        }
    }

    public boolean exists(String key) {
        try (Jedis jedis = redis.getResource()) {
            return jedis.exists(key);
        }
    }

    public void delete(String key) {
        try (Jedis jedis = redis.getResource()) {
            jedis.del(key);
        }
    }
}
