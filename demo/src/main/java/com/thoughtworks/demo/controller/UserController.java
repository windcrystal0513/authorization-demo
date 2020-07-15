package com.thoughtworks.demo.controller;

import com.thoughtworks.demo.common.CommonException;
import com.thoughtworks.demo.common.Result;
import com.thoughtworks.demo.common.RegisterRequest;
import com.thoughtworks.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 跟用户注册登录以及token验证有关的功能
 */
@Slf4j
@RestController
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * API
     * 用户注册
     *
     * @return
     */
    @PostMapping("/register")
    @CrossOrigin(value = "*")
    public Result register(@RequestBody @Valid RegisterRequest registerRequest,
                              BindingResult bindingResult) throws CommonException {
        List <FieldError> allErrors = bindingResult.getFieldErrors();
        for (FieldError fieldError : allErrors) {
            throw new CommonException(HttpStatus.BAD_REQUEST.value(),fieldError.getDefaultMessage());
        }
        Result result = userService.register(registerRequest.getUserName(),
                registerRequest.getPhoneNumber(),
                registerRequest.getEmail(),
                registerRequest.getPassword());
        return result;
    }

    /**
     * 用户登录
     */
    @GetMapping("/login")
    @CrossOrigin(value = "*")

    public Result login(@RequestParam(name = "userName") String userName,
                                @RequestParam(name = "password") String password,
                                HttpServletRequest request) {
        Result result = userService.login(userName, password);
        return result;
    }

    /**
     * 用户验证
     */
    @GetMapping("/safetyVerification")
    @CrossOrigin(value = "*")

    public Result safetyVerification(@RequestParam(name = "tokenString") String tokenString,
                                             HttpServletRequest request) throws Exception {

        Result result = userService.safetyVerification(tokenString);
        return result;
    }

}
