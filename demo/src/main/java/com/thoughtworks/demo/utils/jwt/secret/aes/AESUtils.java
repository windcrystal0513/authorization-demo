package com.thoughtworks.demo.utils.jwt.secret.aes;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * @ClassName: AESUtils
 * @Description: 参数加密解密工具
 */
public class AESUtils {

    public static final Logger LOGGER = LoggerFactory.getLogger(AESUtils.class);

    private static final String AES = "AES";

    private static final String IV_STRING = "dMitHORyqbeYVE0o";

    /**
     * 加密
     *
     * @param content 加密内容
     * @return 密文
     * @throws Exception e
     */
    public static String encrypt(String content , String key) throws Exception {
        byte[] encryptedBytes = new byte[0];
//        try {
            byte[] byteContent = content.getBytes("UTF-8");
            // 注意，为了能与 iOS 统一
            // 这里的 key 不可以使用 KeyGenerator、SecureRandom、SecretKey 生成
            byte[] enCodeFormat = key.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(enCodeFormat, AES);
            byte[] initParam = IV_STRING.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            // 指定加密的算法、工作模式和填充方式
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
            encryptedBytes = cipher.doFinal(byteContent);
            // 同样对加密后数据进行 base64 编码
//        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
//
//            LOGGER.error("AES encrypt Exception,content = {},Exception = {}", content, e.getStackTrace());
//        }
        return new String(Base64.encodeBase64(encryptedBytes));
    }

    /**
     * 解密
     *
     * @param content 密文
     * @return 明文
     * @throws Exception e
     */
    public static String decrypt(String content , String key) {
        // base64 解码
        LOGGER.error("解密 =======================》 "+ content);
        try {
            byte[] encryptedBytes = Base64.decodeBase64(content);
            byte[] enCodeFormat = key.getBytes();
            SecretKeySpec secretKey = new SecretKeySpec(enCodeFormat, AES);
            byte[] initParam = IV_STRING.getBytes();
            IvParameterSpec ivParameterSpec = new IvParameterSpec(initParam);
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
            byte[] result = cipher.doFinal(encryptedBytes);

            return new String(result, "UTF-8");
        } catch (IOException | NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            LOGGER.error("AES decrypt Exception,content = {},Exception = {}", content, e.getStackTrace());
            throw new RuntimeException("解密失败");
        }
    }

    /**
     * 测试生成AES密钥值
     */
//    public static void main(String[] args) {
//        getA221();
//    }

    /**
     * 自动生成AES128位密钥
     */
    public static void getA221(){
        try {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128);//要生成多少位，只需要修改这里即可128, 192或256
            SecretKey sk = kg.generateKey();
            byte[] b = sk.getEncoded();
            String s = byteToHexString(b);
            System.out.println(s);
            System.out.println("十六进制密钥长度为"+s.length());
            System.out.println("二进制密钥的长度为"+s.length()*4);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            System.out.println("没有此算法。");
        }
    }

    /**
     * 二进制byte[]转十六进制string
     */
    public static String byteToHexString(byte[] bytes){
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String strHex=Integer.toHexString(bytes[i]);
            if(strHex.length() > 3){
                sb.append(strHex.substring(6));
            } else {
                if(strHex.length() < 2){
                    sb.append("0" + strHex);
                } else {
                    sb.append(strHex);
                }
            }
        }
        return  sb.toString();
    }


}
