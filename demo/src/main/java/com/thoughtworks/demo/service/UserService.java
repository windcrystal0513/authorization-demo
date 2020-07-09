package com.thoughtworks.demo.service;

import com.thoughtworks.demo.common.Result;
import com.thoughtworks.demo.common.ResultEnum;
import com.thoughtworks.demo.exception.LoginsException;
import com.thoughtworks.demo.exception.RegisterException;
import com.thoughtworks.demo.exception.TokenException;
import com.thoughtworks.demo.jwt.common.*;
import com.thoughtworks.demo.common.Constants;
import com.thoughtworks.demo.domain.User;
import com.thoughtworks.demo.repository.UserRepository;
import com.thoughtworks.demo.utils.MD5WithSaltUtil;
import com.thoughtworks.demo.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;


@RestController
@Service
public class UserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private UserRepository userRepository;

    @Value("${jwt.safety.secret}")
    private String jwtSafetySecret;

    @Value("${jwt.valid.time}")
    private int jwtValidTime;

    /**
     * 登录1024只喵管理系统
     *
     * @param userName 用户名
     * @param password 密码
     *                 return Result
     */
    public Result login(String userName, String password) {
        User userFindByUserName=userRepository.findByUsername(userName);
        User userFindByPhoneNumber=userRepository.findByMobile(userName);
        User userFindByEmail=userRepository.findByEmail(userName);
        if (userFindByUserName == null && userFindByPhoneNumber == null && userFindByEmail == null) {
            logger.error("登录失败：用户不存在");
            throw new LoginsException(400,"用户信息错误");
        }
        if (userFindByUserName!=null){
            String md5=userFindByUserName.getPassword();
            if(MD5WithSaltUtil.verifyPassword(password,md5)){
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),createTokenString(userFindByUserName));
            }
            else{
                logger.error("登录失败：密码错误");
                throw new LoginsException(400,"用户信息错误");
            }
        }
        else if (userFindByPhoneNumber!=null){
            String md5=userFindByPhoneNumber.getPassword();
            if(MD5WithSaltUtil.verifyPassword(password,md5)){
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),createTokenString(userFindByPhoneNumber));
            }
            else{
                logger.error("登录失败：密码错误");
                throw new LoginsException(400,"用户信息错误");
            }
        }
        else if (userFindByEmail!=null){
            String md5=userFindByEmail.getPassword();
            if(MD5WithSaltUtil.verifyPassword(password,md5)){
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(),createTokenString(userFindByEmail));
            }
            else{
                logger.error("登录失败：密码错误");
                throw new LoginsException(400,"用户信息错误");
            }
        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), Constants.LoginTIP);
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
    public Result register  (String userName, String phoneNumber,  String email, String password)throws Exception {
        User userFindByUserName = userRepository.findByUsername(userName);
        User userFindByPhoneNumber = userRepository.findByMobile(phoneNumber);
        User userFindByEmail = userRepository.findByEmail(email);
        if (userFindByUserName != null) {
            logger.error("注册失败：用户名已存在");
            throw new RegisterException(400,"用户名已注册过");
        }
        if (userFindByPhoneNumber != null) {
            logger.error("注册失败：手机号已存在");
            throw new RegisterException(400,"手机号已注册过");
        }
        if (userFindByEmail != null) {
            logger.error("注册失败：邮箱已存在");
            throw new RegisterException(400,"邮箱已注册过");
        }
        User users = new User(userName, phoneNumber, email, MD5WithSaltUtil.gennerateMD5WithSalt(password));
        Date current = new Date();
        users.setCreateTime(current);
        users.setUpdateTime(current);
        users.setStatus(1);
        userRepository.save(users);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), Constants.RegisterTIP);
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
                            .put("id", user.getId())
                            .put("name", user.getUsername())
                            .put("email", user.getEmail())
                            .put("failureTime", FailureTimeUtils.creatValidTime(FailureTime.HOUR, jwtValidTime))
                            )
                    .compact();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if(Jwts.safetyVerification(tokenString)){
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), Constants.VerifyTIP);
        } else{
            logger.error("验证失败");
            throw new TokenException(401,"Token不正确");
        }
    }

}

