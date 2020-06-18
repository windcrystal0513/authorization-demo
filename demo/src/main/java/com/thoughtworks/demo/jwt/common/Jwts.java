package com.thoughtworks.demo.jwt.common;

import com.thoughtworks.demo.domain.ResultEnum;
import com.thoughtworks.demo.jwt.secret.Base64Utils;
import com.thoughtworks.demo.jwt.secret.aes.AESUtils;
import com.thoughtworks.demo.jwt.secret.sm3.SM3Cipher;
import com.thoughtworks.demo.jwt.secret.sm4.SM4Util;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import com.thoughtworks.demo.utils.ResultUtil;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * 类 名: Jwts
 * 描 述:
 * 作 者: yunfeng
 * 创 建: 2020/6/9 : 14:55
 *
 * @author: yunfeng
 */
@Slf4j
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
    private final String jwtSafetySecret = "0dcac1b6ec8843488fbe90e166617e34";

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
        // 头部
        HashMap<String, Object> headerObj = (HashMap<String, Object>) jwts.get("header");
        // 数据
        JwtClaims jwtClaims = (JwtClaims) jwts.get("payload");
        jwtClaims.put("uuid", UUID.randomUUID());
        // 生成签名
        Object jwtSafetySecretObj = headerObj.get("jwtSafetySecret");
        // 从头部信息中去除密钥信息
        headerObj.remove("jwtSafetySecret");
        String jwtSafetySecret = jwtSafetySecretObj == null ? this.jwtSafetySecret : jwtSafetySecretObj.toString();
        Object code = headerObj.get("code");
        String encryptionType = code == null ? "AES" : code.toString();
        // 开始签名
        String signature = dataSignature(headerObj, jwtClaims, encryptionType, jwtSafetySecret);
        // 生成token
        String token = Base64Utils.getBase64(JSONObject.toJSONString(headerObj)) + "."
                + Base64Utils.getBase64(JSONObject.toJSONString(jwtClaims)) + "."
                + signature;
        System.out.println("生成的token为:" + token);
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
//    /**
//     * @author: yunfeng
//     * @demand: 校验token完整性和时效性
//     * @parameters:
//     * @creationDate：
//     * @email:
//     */
//    public static Boolean safetyVerification(String tokenString ) throws Exception {
//        String jwtSafetySecret="y2W89L6BkRAFljhN";
//        // 有坑，转义字符
//        //System.out.println(tokenString);
//        String[] split = tokenString.split("\\.");
//
//        if (split.length != 3) {
//            return ResultUtil.error(ResultEnum.VERIFY_ERROR.getCode(), ResultEnum.VERIFY_ERROR.getMsg());
//        }
//        // 头部信息
//        HashMap<String, Object> obj = JSON.parseObject(Base64Utils.getFromBase64(split[0]), HashMap.class);
//        // 数据信息
//        JwtClaims jwtClaims = JSON.parseObject(Base64Utils.getFromBase64(split[1]), JwtClaims.class);
//        // 签名信息
//        String signature = split[2];
//        System.out.println(obj);
//        System.out.println(jwtClaims);
//        System.out.println(signature);
//        // 验证token是否在有效期内
//        if (jwtClaims.get("failureTime") == null) {
//            return ResultUtil.error(ResultEnum.VERIFY_ERROR.getCode(), ResultEnum.VERIFY_ERROR.getMsg());
//        }
//        if (jwtClaims.get("failureTime") != null) {
//            long failureTime = Long.valueOf(String.valueOf(jwtClaims.get("failureTime")));
//            if (new Date().getTime() > failureTime) {
//                throw new RuntimeException("此token已过有效期");
//            }
//        }
//
//        // 验证数据篡改
//        Object code = obj.get("code");
//        //System.out.println(code);
//        String encryptionType = code == null ? "AES" : code.toString();
//        // 比较签名
//        String signatureNew = dataSignature(obj, jwtClaims, encryptionType, jwtSafetySecret);
//        return signature.equals(signatureNew.replaceAll("\\+"," ")) ? true : false;
//    }


}
