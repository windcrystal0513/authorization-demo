package com.thoughtworks.demo.utils.jwt.secret;

import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;

/**
 * 类 名: Base64Utils
 * 描 述:
 * 作 者: yunfeng
 * 创 建: 2020/6/9 : 14:55
 *
 * @author: yunfeng
 */
public class Base64Utils {

    // 加密
    public static String getBase64(String str) {
        byte[] b = null;
        String s = null;
        try {
            b = str.getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (b != null) {
            s = new String(Base64.encodeBase64(b));
        }
        return s;
    }

    // 解密
    public static String getFromBase64(String s) {
        byte[] b = null;
        String result = null;
        if (s != null) {
//            BASE64Decoder decoder = new BASE64Decoder();
            try {
                b = Base64.decodeBase64(s);
                result = new String(b, "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

}
