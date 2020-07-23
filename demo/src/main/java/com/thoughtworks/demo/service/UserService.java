package com.thoughtworks.demo.service;

import com.thoughtworks.demo.common.Result;
import com.thoughtworks.demo.common.CommonException;
import com.thoughtworks.demo.utils.jwt.common.*;
import com.thoughtworks.demo.common.Constants;
import com.thoughtworks.demo.domain.User;
import com.thoughtworks.demo.repository.UserRepository;
import com.thoughtworks.demo.utils.MD5WithSaltUtil;
import com.thoughtworks.demo.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Slf4j
@RestController
@Service
public class UserService {

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
     * return Result
     */
    public Result login(String userName, String password) {
        User userFindByUserName = userRepository.findByUsername(userName);
        User userFindByPhoneNumber = userRepository.findByMobile(userName);
        User userFindByEmail = userRepository.findByEmail(userName);
        if (userFindByUserName == null && userFindByPhoneNumber == null && userFindByEmail == null) {
            log.error("登录失败：用户不存在");
            throw new CommonException(HttpStatus.BAD_REQUEST.value(), "用户信息错误");
        }
        if (userFindByUserName != null) {
            String md5 = userFindByUserName.getPassword();
            if (MD5WithSaltUtil.verifyPassword(password, md5)) {
                return ResultUtil.success(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), createTokenString(userFindByUserName));
            } else {
                log.error("登录失败：密码错误");
                throw new CommonException(HttpStatus.BAD_REQUEST.value(), "用户信息错误");
            }
        }
        if (userFindByPhoneNumber != null) {
            String md5 = userFindByPhoneNumber.getPassword();
            if (MD5WithSaltUtil.verifyPassword(password, md5)) {
                return ResultUtil.success(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(),createTokenString(userFindByPhoneNumber));
            } else {
                log.error("登录失败：密码错误");
                throw new CommonException(HttpStatus.BAD_REQUEST.value(), "用户信息错误");
            }
        }
        String md5 = userFindByEmail.getPassword();
        if (MD5WithSaltUtil.verifyPassword(password, md5)) {
            return ResultUtil.success(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), createTokenString(userFindByEmail));
        } else {
            log.error("登录失败：密码错误");
            throw new CommonException(HttpStatus.BAD_REQUEST.value(), "用户信息错误");
        }
    }

    /**
     * 注册1024只喵管理系统
     *
     * @param userName    用户名
     * @param phoneNumber 电话
     * @param password    密码
     * @param email       邮箱
     *                    return Result
     */
    public Result register(String userName, String phoneNumber, String email, String password) throws CommonException {
        User userFindByUserName = userRepository.findByUsername(userName);
        User userFindByPhoneNumber = userRepository.findByMobile(phoneNumber);
        User userFindByEmail = userRepository.findByEmail(email);
        if (userFindByUserName != null) {
            log.error("注册失败：用户名已存在");
            throw new CommonException(HttpStatus.BAD_REQUEST.value(), "用户名已注册过");
        }
        if (userFindByPhoneNumber != null) {
            log.error("注册失败：手机号已存在");
            throw new CommonException(HttpStatus.BAD_REQUEST.value(), "手机号已注册过");
        }
        if (userFindByEmail != null) {
            log.error("注册失败：邮箱已存在");
            throw new CommonException(HttpStatus.BAD_REQUEST.value(), "邮箱已注册过");
        }
        Date current = new Date();
        User users = User.builder()
                .username(userName)
                .mobile(phoneNumber)
                .email(email)
                .password(MD5WithSaltUtil.gennerateMD5WithSalt(password))
                .createTime(current)
                .updateTime(current)
                .status(1)
                .build();
        userRepository.save(users);
        return ResultUtil.success(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), Constants.RegisterTIP);
    }

    /**
     * 生成token
     * @param user
     * @return String
     */
    private String createTokenString(User user) {
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
            throw new CommonException(HttpStatus.BAD_REQUEST.value(),"未知错误");
        }
        return jwtToken.replaceAll("\r\n", "");
    }

    /**
     * 验证token是否合格
     *
     * @param tokenString 用户token
     *                    <p>
     *                    return Result
     */

    public Result safetyVerification(String tokenString) throws Exception {
        if (Jwts.safetyVerification(jwtSafetySecret,tokenString)) {
            return ResultUtil.success(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(),  Constants.VerifyTIP);
        } else {
            log.error("验证失败");
            throw new CommonException(HttpStatus.UNAUTHORIZED.value(), "Token不正确");
        }
    }


    public User selectUserInfoByScope(Integer userId, String scope) {
        User user = userRepository.findById(userId);

        //如果是基础权限，则部分信息不返回
        if("basic".equalsIgnoreCase(scope)){
            user.setPassword(null);
            user.setCreateTime(null);
            user.setUpdateTime(null);
            user.setStatus(null);
        }
        return user;
    }

}

