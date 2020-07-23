package com.thoughtworks.demo.service;

import java.util.concurrent.TimeUnit;

/**
 * RedisService

 */
public interface RedisService {
    
    /**
     * 向Redis中存储键值对
     * @param key KEY
     * @param value VALUE
     */
    void set(String key, Object value);

    /**
     * 向Redis中存储键值对，并设置过期时间
     * @param key KEY
     * @param value VALUE
     * @param time 过期时间
     * @param timeUnit 时间单位
     */
    void setWithExpire(String key, Object value, long time, TimeUnit timeUnit);

    /**
     * 从Redis中获取键值对
     * @param key KEY
     * @return K
     */
    <K> K get(String key);

    /**
     * 删除Redis中的某个KEY
     * @param key KEY
     * @return boolean
     */
    boolean delete(String key);
}
