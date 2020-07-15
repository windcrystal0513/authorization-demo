package com.thoughtworks.demo.utils.jwt.common;


import com.thoughtworks.demo.common.CommonException;
import com.thoughtworks.demo.utils.jwt.secret.Base64Utils;
import com.thoughtworks.demo.utils.jwt.secret.aes.AESUtils;
import com.thoughtworks.demo.utils.jwt.secret.sm3.SM3Cipher;
import com.thoughtworks.demo.utils.jwt.secret.sm4.SM4Util;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * 类 名: Jwts
 * 作 者: yunfeng
 * 创 建: 2020/6/9 : 14:55
 *
 * @author: yunfeng
 */

@RestController
@Slf4j
@Service
public class Jwts extends HashMap {

    /**
     * 此处先忽略变量线程安全问题(后期优化)
     */
    private static Jwts jwts;
    static {
        jwts = new Jwts();
    }

    /**
     * 默认加密密钥
     */

    @Value("${jwt.safety.secret}")
    private String jwtSafetySecret;

    /**
     * 指定加密算法和密钥
     *
     * @param header
     * @param jwtSafetySecret
     * @return
     */
    public static Jwts header(Header header, String jwtSafetySecret) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("code", header);
        map.put("jwtSafetySecret", jwtSafetySecret);
        jwts.put("header", map);
        return jwts;
    }

    /**
     * @param jwtClaims
     * @return
     */
    public Jwts payload(JwtClaims jwtClaims) {
        jwts.put("payload", jwtClaims);
        return jwts;
    }

    /**
     * 签名并生成token
     *
     * @return
     */
    public String compact() throws Exception {
        HashMap<String, Object> headerObj = (HashMap<String, Object>) jwts.get("header");
        JwtClaims jwtClaims = (JwtClaims) jwts.get("payload");
        jwtClaims.put("uuid", UUID.randomUUID());
        Object jwtSafetySecretObj = headerObj.get("jwtSafetySecret");
        headerObj.remove("jwtSafetySecret");
        String jwtSafetySecret = jwtSafetySecretObj == null ? this.jwtSafetySecret : jwtSafetySecretObj.toString();
        Object code = headerObj.get("code");
        String encryptionType = code == null ? "AES" : code.toString();
        String signature = dataSignature(headerObj, jwtClaims, encryptionType, jwtSafetySecret);
        String token = Base64Utils.getBase64(JSONObject.toJSONString(headerObj)) + "."
                + Base64Utils.getBase64(JSONObject.toJSONString(jwtClaims)) + "."
                + signature;
        return token;
    }


    /**
     * 生成摘要
     *
     * @param headerObj
     * @param jwtClaims
     * @param encryptionType
     * @param jwtSafetySecret
     * @return
     */
    public static String dataSignature(HashMap<String, Object> headerObj, JwtClaims jwtClaims, String encryptionType, String jwtSafetySecret) throws Exception {
        String dataSignature = null;
        if (encryptionType.equals(Header.AES.name())) {
            dataSignature = AESUtils.encrypt(JSONObject.toJSONString(headerObj) + JSONObject.toJSONString(jwtClaims), jwtSafetySecret);
        } else if (encryptionType.equals(Header.SM3.name())) {
            dataSignature = SM3Cipher.sm3Digest(JSONObject.toJSONString(headerObj) + JSONObject.toJSONString(jwtClaims), jwtSafetySecret);
        } else if (encryptionType.equals(Header.SM4.name())) {
            dataSignature = new SM4Util().encode(JSONObject.toJSONString(headerObj) + JSONObject.toJSONString(jwtClaims), jwtSafetySecret);
        }
        return dataSignature;
    }

    /**
     * @author: yunfeng
     * @demand: 校验token完整性和时效性
     * @parameters:
     */
    public static Boolean safetyVerification(String jwtSafetySecret,String tokenString) throws Exception {

        String[] split = tokenString.split("\\.");

        if (split.length != 3) {
            throw new CommonException(HttpStatus.BAD_REQUEST.value(), "无效的token");
        }
        HashMap<String, Object> headinfo = JSON.parseObject(Base64Utils.getFromBase64(split[0]), HashMap.class);
        JwtClaims jwtClaims = JSON.parseObject(Base64Utils.getFromBase64(split[1]), JwtClaims.class);
        String signature = split[2];
        if (jwtClaims.get("failureTime") != null) {
            long failureTime = Long.parseLong(String.valueOf(jwtClaims.get("failureTime")));
            if (new Date().getTime() > failureTime) {
                throw new CommonException(HttpStatus.UNAUTHORIZED.value(), "Token已过期");
            }
        }


        Object code = headinfo.get("code");
        String encryptionType = code == null ? "AES" : code.toString();
        String signatureNew = dataSignature(headinfo, jwtClaims, encryptionType, jwtSafetySecret);
        return signature.equals(signatureNew.replaceAll("\\+", " "));
    }

}
