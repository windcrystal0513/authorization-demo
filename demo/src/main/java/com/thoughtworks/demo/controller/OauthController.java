package com.thoughtworks.demo.controller;

import com.thoughtworks.demo.common.*;
import com.thoughtworks.demo.domain.AuthAccessToken;
import com.thoughtworks.demo.domain.AuthClientDetails;
import com.thoughtworks.demo.domain.AuthRefreshToken;
import com.thoughtworks.demo.service.AuthorizationService;
import com.thoughtworks.demo.service.UserService;
import com.thoughtworks.demo.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;


import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping(value = "/oauth2.0")
public class OauthController {


    @Autowired
    private UserService userService;
    @Autowired
    private AuthorizationService authorizationService;

    /**
     * 注册需要接入的客户端信息
     * @author yunfeng
     * @date  2020.7.8
     *
     */
    @PostMapping(value = "/clientRegister")
    @CrossOrigin(value = "*")
    public ResponseEntity clientRegister(@RequestParam(name = "clientName") String clientName,
                                         @RequestParam(name = "redirectUri") String redirectUri,
                                         @RequestParam(name = "description") String description,
                                         HttpServletRequest request) throws Exception{
        Result result= authorizationService.register(clientName,redirectUri,description);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    /**
     * 授权页面
     * @author wuyunfeng
     * @date 2020.7.8
     *
     */

//    @RequestMapping("/authorizePage")
//    public ModelAndView authorizePage(HttpServletRequest request){
//        ModelAndView modelAndView = new ModelAndView("authorize");
//
//        //在页面同意授权后的回调地址
//        String redirectUrl = request.getParameter("redirectUri");
//        //客户端ID
//        String clientId = request.getParameter("client_id");
//        //权限范围
//        String scope = request.getParameter("scope");
//
////        if(StringUtils.isNoneBlank(redirectUrl)){
////            HttpSession session = request.getSession();
////            //将回调地址添加到session中
////            session.setAttribute(Constants.SESSION_AUTH_REDIRECT_URL,redirectUrl);
////        }
//
//        //查询请求授权的客户端信息
//        AuthClientDetails clientDetails = authorizationService.selectClientDetailsByClientId(clientId);
//        modelAndView.addObject("clientId", clientId);
//        modelAndView.addObject("clientName", clientDetails.getClientName());
//        modelAndView.addObject("scope", scope);
//
//        return modelAndView;
//    }
//
//    /**
//     * 授权页面
//     * @author wuyunfeng
//     * @date 2020.7.8
//     *
//     */
//    @PostMapping(value = "/agree")
//    @ResponseBody
//    public Map<String,Object> agree(HttpServletRequest request){
//        Map<String,Object> result = new HashMap<>(2);
//        HttpSession session = request.getSession();
//
//
//
//        //客户端ID
//        String clientIdStr = request.getParameter("client_id");
//        //权限范围
//        String scopeStr = request.getParameter("scope");
//
//        if(StringUtils.isNoneBlank(clientIdStr) && StringUtils.isNoneBlank(scopeStr)){
//            User user = (User) session.getAttribute(Constants.SESSION_USER);
//
//            //1. 向数据库中保存授权信息
//            boolean saveFlag = authorizationService.saveAuthClientUser(user.getId(), clientIdStr, scopeStr);
//
//            //2. 返回给页面的数据
//            if(saveFlag){
//                result.put("code",200);
//
//                //授权成功之后的回调地址
//                String redirectUrl = (String) session.getAttribute(Constants.SESSION_AUTH_REDIRECT_URL);
//                session.removeAttribute(Constants.SESSION_AUTH_REDIRECT_URL);
//
//                if(StringUtils.isNoneBlank(redirectUrl)){
//                    result.put("redirect_uri", redirectUrl);
//                }
//            }else{
//                result.put("msg", "授权失败！");
//            }
//        }else{
//            result.put("msg", "请求参数不能为空！");
//        }
//
//        return result;
//    }
    /**
     * 获取Authorization Code
     * @author wuyunfeng
     * @date 2020.7.8
     *
     */
    @RequestMapping("/authorize")
    public ModelAndView authorize(HttpServletRequest request){
//        HttpSession session = request.getSession();
//        User user = (User) session.getAttribute(Constants.SESSION_USER);

        //客户端ID
        String clientIdStr = request.getParameter("client_id");
        //权限范围
        String scopeStr = request.getParameter("scope");
        //回调URL
        String redirectUri = request.getParameter("redirect_uri");
        //status，用于防止CSRF攻击（非必填）
        String status = request.getParameter("status");

        //生成Authorization Code
        String authorizationCode = authorizationService.createAuthorizationCode(clientIdStr, scopeStr);

        String params = "?code=" + authorizationCode;
        if(StringUtils.isNoneBlank(status)){
            params = params + "&status=" + status;
        }

        return new ModelAndView("redirect:" + redirectUri + params);
    }

    /**
     * 通过Authorization Code获取Access Token
     * @author wuyunfeng
     * @date 2020.7.8
     */
    @RequestMapping(value = "/token")
    @CrossOrigin(value = "*")
    @ResponseBody
    public ResponseEntity token(HttpServletRequest request)throws Exception{
        Map<String,Object> result = new HashMap<>(8);

        //授权方式
        String grantType = request.getParameter("grant_type");
        //前面获取的Authorization Code
        String code = request.getParameter("code");
        //客户端ID
        String clientIdStr = request.getParameter("client_id");
        //接入的客户端的密钥
        String clientSecret = request.getParameter("client_secret");
        //回调URL
        String redirectUri = request.getParameter("redirect_uri");

        //校验授权方式
        if(!GrantTypeEnum.AUTHORIZATION_CODE.getType().equals(grantType)){
            log.error("请求的Authorization Code、Accesen、Refresh Token等信息是无效的。");
            throw new CommonException(HttpStatus.BAD_REQUEST.value(),"请求的Authorization Code、Access Token、Refresh Token等信息是无效的。");
        }

        AuthClientDetails savedClientDetails = authorizationService.selectClientDetailsByClientId(clientIdStr);
        //校验请求的客户端秘钥和已保存的秘钥是否匹配
        if(!(savedClientDetails != null && savedClientDetails.getClientSecret().equals(clientSecret))){
            log.error("client_id或client_secret 无效");
            throw new CommonException(HttpStatus.BAD_REQUEST.value(),"请求的client_id或client_secret参数无效。");
        }

        //校验回调URL
        if(!savedClientDetails.getRedirectUri().equals(redirectUri)){
            log.error("URL不正确");
            throw new CommonException(HttpStatus.UNAUTHORIZED.value(),"请求的redirect_uri所在的域名与开发者注册应用时所填写的域名不匹配。");
        }

//            //从Redis获取允许访问的用户权限范围
//            String scope = redisService.get(code + ":scope");
//            //从Redis获取对应的用户信息
//            User user = redisService.get(code + ":user");

        //如果能够通过Authorization Code获取到对应的用户信息，则说明该Authorization Code有效

        //过期时间
        Long expiresIn = DateUtils.dayToSecond(ExpireEnum.ACCESS_TOKEN.getTime());

        //生成Access Token
        String accessTokenStr = authorizationService.createAccessToken( savedClientDetails, grantType, expiresIn);
        //查询已经插入到数据库的Access Token
        AuthAccessToken authAccessToken = authorizationService.selectByAccessToken(accessTokenStr);
        //生成Refresh Token
        String refreshTokenStr = authorizationService.createRefreshToken( authAccessToken);

        //返回数据

        result.put("access_token", authAccessToken.getAccessToken());
        result.put("refresh_token", refreshTokenStr);
        result.put("expires_in", expiresIn);
        result.put("scope", "basic");
        return ResponseEntity.status(HttpStatus.OK).body(result);

    }


    /**
     * 通过Refresh Token刷新Access Token
     * @author wuyunfeng
     * @date 2020.7.8
     */
    @RequestMapping(value = "/refreshToken")
    @ResponseBody
    public ResponseEntity refreshToken(HttpServletRequest request){
        Map<String,Object> result = new HashMap<>(8);

        //获取Refresh Token
        String refreshTokenStr = request.getParameter("refresh_token");

        AuthRefreshToken authRefreshToken = authorizationService.selectByRefreshToken(refreshTokenStr);

        if(authRefreshToken != null) {
            Long savedExpiresAt = authRefreshToken.getExpiresIn();
            //过期日期
            LocalDateTime expiresDateTime = DateUtils.ofEpochSecond(savedExpiresAt, null);
            //当前日期
            LocalDateTime nowDateTime = DateUtils.now();

            //如果Refresh Token已经失效，则需要重新生成
            if (expiresDateTime.isBefore(nowDateTime)) {
                log.error("请求的Access Token或Refresh Token已过期。");
                throw new CommonException(HttpStatus.BAD_REQUEST.value(),"请求的Access Token或Refresh Token已过期。");
            } else {
                //获取存储的Access Token
                AuthAccessToken authAccessToken = authorizationService.selectByAccessId(authRefreshToken.getTokenId());
                //获取对应的客户端信息
                AuthClientDetails savedClientDetails = authorizationService.selectClientDetailsById(authAccessToken.getClientId());
//                //获取对应的用户信息
//                User user = userService.selectByUserId(authAccessToken.getUserId());

                //新的过期时间
                Long expiresIn = DateUtils.dayToSecond(ExpireEnum.ACCESS_TOKEN.getTime());
                //生成新的Access Token
                String newAccessTokenStr = authorizationService.createAccessToken(savedClientDetails, authAccessToken.getGrantType(), expiresIn);

                //返回数据
                result.put("access_token", newAccessTokenStr);
                result.put("refresh_token", refreshTokenStr);
                result.put("expires_in", expiresIn);
                result.put("scope", authAccessToken.getScope());
                return ResponseEntity.status(HttpStatus.OK).body(result);
            }
        }else {
            log.error("请求的Authorization Code、Access Token、Refresh Token等信息是无效的。");
            throw new CommonException(HttpStatus.BAD_REQUEST.value(),"请求的Authorization Code、Access Token、Refresh Token等信息是无效的。");
        }
    }

}
