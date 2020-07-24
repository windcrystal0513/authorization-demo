package com.thoughtworks.demo.controller;


import com.alibaba.fastjson.JSON;
import com.thoughtworks.demo.common.CommonException;
import com.thoughtworks.demo.domain.AuthAccessToken;
import com.thoughtworks.demo.domain.User;
import com.thoughtworks.demo.service.AuthorizationService;
import com.thoughtworks.demo.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;


/**
 * 通过Access Token访问的API服务
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class ApiController {
    @Resource(name = "authorizationServiceImpl")
    private AuthorizationService authorizationService;

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/users/getInfo")

    public String getUserInfo(HttpServletRequest request) {
        String accessToken = request.getParameter("access_token");
        AuthAccessToken authAccessToken = authorizationService.selectByAccessToken(accessToken);

        if (authAccessToken != null) {
            User user = userService.selectUserInfoByScope(authAccessToken.getUserId(), authAccessToken.getScope());

            return JSON.toJSON(user).toString();
        } else {
            log.error("access_token无效");
            throw new CommonException(HttpStatus.UNAUTHORIZED.value(), "未授权访问");
        }
    }

}
