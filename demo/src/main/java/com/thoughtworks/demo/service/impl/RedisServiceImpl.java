package com.thoughtworks.demo.service.impl;

import com.thoughtworks.demo.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 *
 */
@Service("redisServiceImpl")
public class RedisServiceImpl implements RedisService{

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void set(String key, Object value) {
        ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();
        valueOperation.set(key, value);
    }

    @Override
    public void setWithExpire(String key, Object value, long time, TimeUnit timeUnit) {
        BoundValueOperations<String, Object> boundValueOperations = redisTemplate.boundValueOps(key);
        boundValueOperations.set(value);
        boundValueOperations.expire(time,timeUnit);
    }

    @Override
    public <K> K get(String key) {
        ValueOperations<String, Object> valueOperation = redisTemplate.opsForValue();

        return (K) valueOperation.get(key);
    }

    @Override
    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }
}
