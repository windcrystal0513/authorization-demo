package com.thoughtworks.demo.utils;

import com.thoughtworks.demo.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(TokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Map handleTokenException(TokenException e){
        Map<String, Object> map = new HashMap<>();
        map.put("code", e.getCode());
        map.put("msg", e.getMsg());
        return map;
    }
    @ExceptionHandler(RegisterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map handleRegisterException(RegisterException e){
        Map<String, Object> map = new HashMap<>();
        map.put("code", e.getCode());
        map.put("msg", e.getMsg());
        return map;
    }
    @ExceptionHandler(LoginsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map handleLoginsException(LoginsException e){
        Map<String, Object> map = new HashMap<>();
        map.put("code", e.getCode());
        map.put("msg", e.getMsg());
        return map;
    }
    @ExceptionHandler(Oauth2Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map handleOauth2Exception(Oauth2Exception e){
        Map<String, Object> map = new HashMap<>();
        map.put("code", e.getCode());
        map.put("msg", e.getMsg());
        return map;
    }
}
