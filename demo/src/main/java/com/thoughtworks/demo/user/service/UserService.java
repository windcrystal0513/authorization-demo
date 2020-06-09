package com.thoughtworks.demo.user.service;

import com.thoughtworks.demo.domain.Result;
import com.thoughtworks.demo.domain.ResultEnum;
import com.thoughtworks.demo.user.domain.User;
import com.thoughtworks.demo.user.repository.UserRepository;
import com.thoughtworks.demo.utils.ResultUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class    UserService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private UserRepository userRepository;
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
        User user = userRepository.findByUserNameAndPassword(userName, password);
        if (user == null) {
            logger.error("登录失败：用户不存在");
            return ResultUtil.error(ResultEnum.LOGIN_ERROR.getCode(), ResultEnum.LOGIN_ERROR.getMsg());

        }
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "用户登录成功");
    }

    /**
     * 注册1024只喵管理系统
     *
     * @param userName 用户名
     * @param password 密码
     * @param email    邮箱
     *                 return Result
     */
    public Result register(String userName, String password, String email) {
        User user = userRepository.findByUserName(userName);
        if (user != null) {
            logger.error("注册失败：用户已存在");
            return ResultUtil.error(ResultEnum.LOGIN_ERROR.getCode(), ResultEnum.LOGIN_ERROR.getMsg());
        }
        User users = new User(userName, password,email);
        userRepository.save(users);
        return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), "用户注册成功");
    }

}
