package com.thoughtworks.demo.utils.jwt.common;

import java.util.HashMap;
import java.util.Objects;

/**
 * 类 名: JwtClaims
 * 描 述:
 * 作 者: yunfeng
 * 创 建: 2020/6/9 : 14:55
 *
 * @author: yunfeng
 */
public class JwtClaims extends HashMap {

    public JwtClaims() {
        this.put(ID, null);
        this.put(NAME, null);
        this.put(EMAIL, null);
        this.put(FAILURETIME, null);
    }

    String ID = "id";
    String NAME = "name";
    String EMAIL = "email";
    /**
     * 有效期
     */
    String FAILURETIME = "failureTime";

    public JwtClaims put(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * 重写hashCode方法
     *
     * @return
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this);
    }
}
