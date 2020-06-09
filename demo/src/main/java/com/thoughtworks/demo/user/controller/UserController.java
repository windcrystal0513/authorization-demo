package com.thoughtworks.demo.user.controller;

import com.thoughtworks.demo.domain.Result;
import com.thoughtworks.demo.domain.ResultEnum;
import com.thoughtworks.demo.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**

 */
@RestController
@RequestMapping(value = "/user")
public class UserController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    UserService userService;

    /**
     * API
     * 用户注册
     */
    @PostMapping("/register")
    @CrossOrigin(value = "*")
    public ResponseEntity register(@RequestParam(name = "userName") String userName,
                                @RequestParam(name = "password") String password,
                                @RequestParam(name = "email") String email,

                                HttpServletRequest request) {
        //logger.info(HttpUtil.getHeaders(request));
        Result result = userService.register(userName, password, email);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 用户登录
     */
    @GetMapping("/login")
    @CrossOrigin(value = "*")

    public ResponseEntity login(@RequestParam(name = "userName") String userName,
                                @RequestParam(name = "password") String password,
                                HttpServletRequest request) {
        //logger.info(HttpUtil.getHeaders(request));
        Result result = userService.login(userName, password);
        if (!result.getCode().equals(ResultEnum.SUCCESS.getCode())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
