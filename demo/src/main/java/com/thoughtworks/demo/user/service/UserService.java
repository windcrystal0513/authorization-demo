package com.thoughtworks.demo.user.service;

import com.alibaba.fastjson.JSON;
import com.thoughtworks.demo.jwt.common.*;
import com.thoughtworks.demo.domain.Result;
import com.thoughtworks.demo.domain.ResultEnum;
import com.thoughtworks.demo.jwt.secret.Base64Utils;
import com.thoughtworks.demo.user.domain.User;
import com.thoughtworks.demo.user.repository.UserRepository;
import com.thoughtworks.demo.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;

@Service
public class    UserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.safety.secret}")
    private String jwtSafetySecret;

    @Value("${jwt.valid.time}")
    private int jwtValidTime;
//    @Autowired
//    private UserLogRepository userLogRepository;
    /**
     * 登录1024只喵管理系统
     *
     * @param userName 用户名
     * @param password 密码
     *                 return Result
     */
    public Result login(String userName, String password) {
        User user1=userRepository.findByUserName(userName);
        User user2=userRepository.findByPhoneNumber(userName);
        User user3=userRepository.findByEmail(userName);
        User user4 = userRepository.findByUserNameAndPassword(userName, password);
        User user5 = userRepository.findByPhoneNumberAndPassword(userName, password);
        User user6 = userRepository.findByEmailAndPassword(userName, password);
        //String userss=user.getUserId()+user.getUserName();

        if (user1 == null && user2 == null && user3 == null) {
            logger.error("登录失败：用户不存在");
            return ResultUtil.error(ResultEnum.LOGIN_ERROR.getCode(), ResultEnum.LOGIN_ERROR.getMsg());
        }
        if (user4 == null && user5 == null && user6 == null) {
            logger.error("登录失败：密码错误");
            return ResultUtil.error(ResultEnum.LOGIN_ERROR_1.getCode(), ResultEnum.LOGIN_ERROR_1.getMsg());
        }
        if(user1!=null){
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), createTokenString(user1));
        }
        if(user2!=null){
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), createTokenString(user2));
        }
        if(user3!=null){
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), createTokenString(user3));
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "登录成功");
    }

    /**
     * 注册1024只喵管理系统
     *
     * @param userName 用户名
     * @param phoneNumber 电话
     * @param password 密码
     * @param email    邮箱
     *                 return Result
     */
    public Result register(String userName, String phoneNumber,  String email, String password) {
        User user1 = userRepository.findByUserName(userName);
        User user2 = userRepository.findByPhoneNumber(phoneNumber);
        User user3 = userRepository.findByEmail(email);
        if (user1 != null) {
            logger.error("注册失败：用户名已存在");
            return ResultUtil.error(ResultEnum.REGISTER_ERROR_1.getCode(), ResultEnum.REGISTER_ERROR_1.getMsg());
        }
        if (user2 != null) {
            logger.error("注册失败：电话已存在");
            return ResultUtil.error(ResultEnum.REGISTER_ERROR_2.getCode(), ResultEnum.REGISTER_ERROR_2.getMsg());
        }
        if (user3 != null) {
            logger.error("注册失败：邮箱已存在");
            return ResultUtil.error(ResultEnum.REGISTER_ERROR_3.getCode(), ResultEnum.REGISTER_ERROR_3.getMsg());
        }
        User users = new User(userName, phoneNumber, email, password);
        userRepository.save(users);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "用户注册成功");
    }
    /**
     * 生成token
     *
     * @param user
     * @return
     */
    public String createTokenString(User user) {
        String jwtToken = null;
        try {
            jwtToken = Jwts.header(Header.SM4, jwtSafetySecret)
                    .payload(new JwtClaims()
                            .put("id", user.getUserId())
                            .put("name", user.getUserName())
                            .put("email", user.getEmail())
                            .put("failureTime", FailureTimeUtils.creatValidTime(FailureTime.HOUR, jwtValidTime))
                            )
                    .compact();
//            System.out.println('1');
//            System.out.println(jwtToken);
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.println(jwtToken);
//        System.out.println(jwtToken.replaceAll("\r\n",""));
        return jwtToken.replaceAll("\r\n","");
    }

    /**
     * 验证token是否合格
     *
     * @param tokenString 用户token
     *
     *                 return Result
     */

    public Result safetyVerification(String tokenString) throws Exception{
        String jwtSafetySecret="y2W89L6BkRAFljhN";
        // 有坑，转义字符
        //System.out.println(tokenString);
        String[] split = tokenString.split("\\.");

        if (split.length != 3) {
            return ResultUtil.error(ResultEnum.VERIFY_ERROR.getCode(), ResultEnum.VERIFY_ERROR.getMsg());
        }
        // 头部信息
        HashMap<String, Object> obj = JSON.parseObject(Base64Utils.getFromBase64(split[0]), HashMap.class);
        // 数据信息
        JwtClaims jwtClaims = JSON.parseObject(Base64Utils.getFromBase64(split[1]), JwtClaims.class);
        // 签名信息
        String signature = split[2];
        System.out.println(obj);
        System.out.println(jwtClaims);
        System.out.println(signature);
        // 验证token是否在有效期内
        if (jwtClaims.get("failureTime") == null) {
            return ResultUtil.error(ResultEnum.VERIFY_ERROR.getCode(), ResultEnum.VERIFY_ERROR.getMsg());
        }
        if (jwtClaims.get("failureTime") != null) {
            long failureTime = Long.valueOf(String.valueOf(jwtClaims.get("failureTime")));
            if (new Date().getTime() > failureTime) {
                return ResultUtil.error(ResultEnum.VERIFY_ERROR_1.getCode(), ResultEnum.VERIFY_ERROR_1.getMsg());
            }
        }

        // 验证数据篡改
        Object code = obj.get("code");
        //System.out.println(code);
        String encryptionType = code == null ? "AES" : code.toString();
        // 比较签名
        String signatureNew = Jwts.dataSignature(obj, jwtClaims, encryptionType, jwtSafetySecret);
        if(signature.equals(signatureNew.replaceAll("\\+"," "))){
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "用户验证成功");
        } else{
            logger.error("验证失败");
            return ResultUtil.error(ResultEnum.VERIFY_ERROR.getCode(), ResultEnum.VERIFY_ERROR.getMsg());
        }
    }
}

