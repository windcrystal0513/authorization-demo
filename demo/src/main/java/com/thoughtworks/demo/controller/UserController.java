package com.thoughtworks.demo.controller;

import com.thoughtworks.demo.common.Result;
import com.thoughtworks.demo.service.UserService;
import com.thoughtworks.demo.utils.HttpUtil;
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
                                   @RequestParam(name = "phoneNumber") String phoneNumber,
                                   @RequestParam(name = "email") String email,
                                @RequestParam(name = "password") String password,
                                HttpServletRequest request) throws Exception{
        logger.info(HttpUtil.getHeaders(request));
        Result result = userService.register(userName, phoneNumber, email, password);
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
        Result result = userService.login(userName, password);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 用户验证
     */
    @GetMapping("/safetyVerification")
    @CrossOrigin(value = "*")

        public ResponseEntity safetyVerification(@RequestParam(name = "tokenString") String tokenString,
                                HttpServletRequest request) throws Exception{

        Result result = userService.safetyVerification(tokenString);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
