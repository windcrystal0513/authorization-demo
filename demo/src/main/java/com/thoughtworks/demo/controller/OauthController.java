package com.thoughtworks.demo.controller;

import com.thoughtworks.demo.common.*;
import com.thoughtworks.demo.domain.AuthAccessToken;
import com.thoughtworks.demo.domain.AuthClientDetails;
import com.thoughtworks.demo.domain.AuthRefreshToken;
import com.thoughtworks.demo.domain.User;
import com.thoughtworks.demo.service.AuthorizationService;
import com.thoughtworks.demo.service.UserService;
import com.thoughtworks.demo.service.RedisService;
import com.thoughtworks.demo.utils.DateUtils;
import com.thoughtworks.demo.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "/oauth2.0")
public class OauthController {


    @Resource(name = "redisServiceImpl")
    private RedisService redisService;

    @Resource(name = "authorizationServiceImpl")
    private AuthorizationService authorizationService;

    /**
     * 注册需要接入的客户端信息
     *
     * @author yunfeng
     * @date 2020.7.8
     */
    @PostMapping(value = "/clientRegister")
    @CrossOrigin(value = "*")
    public Result clientRegister(@RequestParam(name = "clientName") String clientName,
                                 @RequestParam(name = "redirectUri") String redirectUri,
                                 @RequestParam(name = "description") String description,
                                 HttpServletRequest request) throws Exception {
        Result result = authorizationService.register(clientName, redirectUri, description);

        return result;
    }

    /**
     * 授权页面
     *
     * @author wuyunfeng
     * @date 2020.7.8
     */

    @RequestMapping("/authorizePage")
    public ModelAndView authorizePage(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView("authorize");
        String redirectUrl = request.getParameter("redirectUri");
        String clientId = request.getParameter("client_id");
        String scope = request.getParameter("scope");
        if (StringUtils.isNoneBlank(redirectUrl)) {
            HttpSession session = request.getSession();
            session.setAttribute(Constants.SESSION_AUTH_REDIRECT_URL, redirectUrl);
        }
        AuthClientDetails clientDetails = authorizationService.selectClientDetailsByClientId(clientId);
        if (clientDetails != null) {
            modelAndView.addObject("clientId", clientId);
            modelAndView.addObject("clientName", clientDetails.getClientName());
            modelAndView.addObject("scope", scope);
        }
        return modelAndView;
    }

    /**
     * 授权页面
     *
     * @author wuyunfeng
     * @date 2020.7.8
     */
    @PostMapping(value = "/agree")
    public Map<String, Object> agree(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>(2);
        HttpSession session = request.getSession();
        String clientIdStr = request.getParameter("client_id");
        String scopeStr = request.getParameter("scope");

        if (StringUtils.isNoneBlank(clientIdStr) && StringUtils.isNoneBlank(scopeStr)) {
            User user = (User) session.getAttribute(Constants.SESSION_USER);
            if (user == null) {
                log.error("用户没有登录，没有user的session");
                throw new CommonException(HttpStatus.BAD_REQUEST.value(), "用户没有登录");
            }
            boolean saveFlag = authorizationService.saveAuthClientUser(user.getId(), clientIdStr, scopeStr);
            if (saveFlag) {
                result.put("code", 200);
                String redirectUrl = (String) session.getAttribute(Constants.SESSION_AUTH_REDIRECT_URL);
                session.removeAttribute(Constants.SESSION_AUTH_REDIRECT_URL);

                if (StringUtils.isNoneBlank(redirectUrl)) {
                    result.put("redirect_uri", redirectUrl);
                }
            } else {
                System.out.println("failed");
                result.put("msg", "授权失败！");
            }
        } else {
            result.put("msg", "请求参数不能为空！");
        }
        return result;
    }

    /**
     * 获取Authorization Code
     *
     * @author wuyunfeng
     * @date 2020.7.8
     * status是用于防止CSRF攻击（非必填）
     */
    @RequestMapping("/authorize")
    public ModelAndView authorize(HttpServletRequest request) {
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Constants.SESSION_USER);
        String clientIdStr = request.getParameter("client_id");
        String scopeStr = request.getParameter("scope");
        String redirectUri = request.getParameter("redirect_uri");
        String status = request.getParameter("status");
        String authorizationCode = authorizationService.createAuthorizationCode(clientIdStr, scopeStr, user);
        String params = "?code=" + authorizationCode;
        if (StringUtils.isNoneBlank(status)) {
            params = params + "&status=" + status;
        }
        return new ModelAndView("redirect:" + redirectUri + params);
    }

    /**
     * 通过Authorization Code获取Access Token
     *
     * @author wuyunfeng
     * @date 2020.7.8
     */
    @RequestMapping(value = "/token")
    @CrossOrigin(value = "*")
    @ResponseBody
    public Result token(HttpServletRequest request) throws Exception {
        Map<String, Object> result = new HashMap<>(8);
        String grantType = request.getParameter("grant_type");
        String code = request.getParameter("code");
        String clientIdStr = request.getParameter("client_id");
        String clientSecret = request.getParameter("client_secret");
        String redirectUri = request.getParameter("redirect_uri");

        if (!GrantTypeEnum.AUTHORIZATION_CODE.getType().equals(grantType)) {
            log.error("请求的Authorization Code、Accesen、Refresh Token等信息是无效的。");
            throw new CommonException(HttpStatus.BAD_REQUEST.value(), "请求的Authorization Code、Access Token、Refresh Token等信息是无效的。");
        }
        AuthClientDetails savedClientDetails = authorizationService.selectClientDetailsByClientId(clientIdStr);
        if (!(savedClientDetails != null && savedClientDetails.getClientSecret().equals(clientSecret))) {
            log.error("client_id或client_secret 无效");
            throw new CommonException(HttpStatus.BAD_REQUEST.value(), "请求的client_id或client_secret参数无效。");
        }
        if (!savedClientDetails.getRedirectUri().equals(redirectUri)) {
            log.error("URL不正确");
            throw new CommonException(HttpStatus.UNAUTHORIZED.value(), "请求的redirect_uri所在的域名与开发者注册应用时所填写的域名不匹配。");
        }

        String scope = redisService.get(code + ":scope");
        User user = redisService.get(code + ":user");
        if (StringUtils.isNoneBlank(scope) && user != null) {
            Long expiresIn = DateUtils.dayToSecond(ExpireEnum.ACCESS_TOKEN.getTime());

            String accessTokenStr = authorizationService.createAccessToken(user, savedClientDetails, grantType, scope, expiresIn);
            AuthAccessToken authAccessToken = authorizationService.selectByAccessToken(accessTokenStr);
            String refreshTokenStr = authorizationService.createRefreshToken(user, authAccessToken);

            result.put("access_token", authAccessToken.getAccessToken());
            result.put("refresh_token", refreshTokenStr);
            result.put("expires_in", expiresIn);
            result.put("scope", "basic");

            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), result);
        } else {
            log.error("授权失败");
            throw new CommonException(HttpStatus.UNAUTHORIZED.value(), "授权失败");
        }
    }


    /**
     * 通过Refresh Token刷新Access Token
     *
     * @author wuyunfeng
     * @date 2020.7.8
     */
    @RequestMapping(value = "/refreshToken")
    @ResponseBody
    public Result refreshToken(HttpServletRequest request) {
        Map<String, Object> result = new HashMap<>(8);

        String refreshTokenStr = request.getParameter("refresh_token");
        AuthRefreshToken authRefreshToken = authorizationService.selectByRefreshToken(refreshTokenStr);
        if (authRefreshToken != null) {
            Long savedExpiresAt = authRefreshToken.getExpiresIn();
            LocalDateTime expiresDateTime = DateUtils.ofEpochSecond(savedExpiresAt, null);
            LocalDateTime nowDateTime = DateUtils.now();
            if (expiresDateTime.isBefore(nowDateTime)) {
                log.error("请求的Access Token或Refresh Token已过期。");
                throw new CommonException(HttpStatus.BAD_REQUEST.value(), "请求的Access Token或Refresh Token已过期。");
            } else {
                AuthAccessToken authAccessToken = authorizationService.selectByAccessId(authRefreshToken.getTokenId());
                AuthClientDetails savedClientDetails = authorizationService.selectClientDetailsById(authAccessToken.getClientId());
                User user = authorizationService.selectByUserId(authAccessToken.getUserId());

                Long expiresIn = DateUtils.dayToSecond(ExpireEnum.ACCESS_TOKEN.getTime());
                String newAccessTokenStr = authorizationService.createAccessToken(user, savedClientDetails
                        , authAccessToken.getGrantType(), authAccessToken.getScope(), expiresIn);
                result.put("access_token", newAccessTokenStr);
                result.put("refresh_token", refreshTokenStr);
                result.put("expires_in", expiresIn);
                result.put("scope", authAccessToken.getScope());
                return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), result);
            }
        } else {
            log.error("请求的Authorization Code、Access Token、Refresh Token等信息是无效的。");
            throw new CommonException(HttpStatus.BAD_REQUEST.value(), "请求的Authorization Code、Access Token、Refresh Token等信息是无效的。");
        }
    }
}
