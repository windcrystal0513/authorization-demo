package com.thoughtworks.demo.service.impl;

import com.thoughtworks.demo.common.Constants;
import com.thoughtworks.demo.common.ExpireEnum;
import com.thoughtworks.demo.common.Result;
import com.thoughtworks.demo.common.ResultEnum;
import com.thoughtworks.demo.common.CommonException;
import com.thoughtworks.demo.repository.*;
import com.thoughtworks.demo.domain.*;
import com.thoughtworks.demo.service.AuthorizationService;
import com.thoughtworks.demo.utils.DateUtils;
import com.thoughtworks.demo.utils.EncryptUtils;
import com.thoughtworks.demo.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 *
 */
@RestController
@Slf4j
@Service("authorizationServiceImpl")
public class AuthorizationServiceImpl implements AuthorizationService {

    @Autowired
    private AuthClientDetailsRepository authClientDetailsRepository;
    @Autowired
    private AuthScopeRepository authScopeRepository;
    @Autowired
    private AuthClientUserRepository authClientUserRepository;
    @Autowired
    private AuthAccessTokenRepository authAccessTokenRepository;
    @Autowired
    private AuthRefreshTokenRepository authRefreshTokenRepository;


    /**
     * client在IDP处登记信息
     *
     * @param clientName
     * @param redirectUri
     * @param description
     * @return Result
     */
    @Override
    public Result register(String clientName, String redirectUri, String description) throws CommonException {
        if (!"".equals(clientName) && !"".equals(redirectUri) && !"".equals(description)) {
            String clientId = EncryptUtils.getRandomStr1(24);

            AuthClientDetails savedClientDetails = authClientDetailsRepository.findByClientId(clientId);
            for (int i = 0; i < 10; i++) {
                if (savedClientDetails == null) {
                    break;
                } else {
                    clientId = EncryptUtils.getRandomStr1(24);
                    savedClientDetails = authClientDetailsRepository.findByClientId(clientId);
                }
            }
            String clientSecret = EncryptUtils.getRandomStr1(32);

            Date current = new Date();

//            AuthClientDetails clientDetails = new AuthClientDetails();
//
//            clientDetails.setClientName(clientName);
//            clientDetails.setRedirectUri(redirectUri);
//            clientDetails.setDescription(description);
//            clientDetails.setClientId(clientId);
//            clientDetails.setClientSecret(clientSecret);
//            //clientDetails.setCreateUser(user.getId());
//            clientDetails.setCreateTime(current);
//            //clientDetails.setUpdateUser(user.getId());
//            clientDetails.setUpdateTime(current);
//            clientDetails.setStatus(1);


            authClientDetailsRepository
                    .save(AuthClientDetails
                            .builder()
                            .clientId(clientId)
                            .clientName(clientName)
                            .redirectUri(redirectUri)
                            .description(description)
                            .clientSecret(clientSecret)
                            .createTime(current)
                            .updateTime(current)
                            .status(1)
                            .build()
                    );
            return ResultUtil.success(ResultEnum.SUCCESS.getCode(), ResultEnum.SUCCESS.getMsg(), Constants.RegisterTIP);
        } else {
            log.error("注册失败：信息不完整");
            throw new CommonException(HttpStatus.BAD_REQUEST.value(), "信息不完整");
        }
    }


    public AuthClientDetails selectClientDetailsById(Integer id) {
        return authClientDetailsRepository.findById(id);
    }

    @Override
    public AuthClientDetails selectClientDetailsByClientId(String clientId) {
        return authClientDetailsRepository.findByClientId(clientId);
    }

    @Override
    public AuthAccessToken selectByAccessToken(String accessToken) {
        return authAccessTokenRepository.findByAccessToken(accessToken);
    }

    @Override
    public AuthAccessToken selectByAccessId(Integer id) {
        return authAccessTokenRepository.findById(id);
    }

    @Override
    public AuthRefreshToken selectByRefreshToken(String refreshToken) {
        return authRefreshTokenRepository.findByRefreshToken(refreshToken);
    }
//
//    @Override
//    public boolean saveAuthClientUser(Integer userId, String clientIdStr, String scopeStr) {
//        AuthClientDetails clientDetails = authClientDetailsRepository.findByClientId(clientIdStr);
//        AuthScope scope = authScopeRepository.findByScopeName(scopeStr);
//
//        if(clientDetails != null && scope != null){
//            AuthClientUser clientUser = authClientUserRepository.findByClientIdAndUserIdAndScopeId(clientDetails.getId(), userId, scope.getId());
//            //如果数据库中不存在记录，则插入
//            if(clientUser == null){
//                clientUser = new AuthClientUser();
//                clientUser.setUserId(userId);
//                clientUser.setAuthClientId(clientDetails.getId());
//                clientUser.setAuthScopeId(scope.getId());
//                authClientUserRepository.save(clientUser);
//            }
//
//            return true;
//        }else{
//            return false;
//        }
//    }

    /**
     * 生成authorizationCode
     *
     * @param clientIdStr
     * @param scopeStr
     * @return String
     */
    @Override
    public String createAuthorizationCode(String clientIdStr, String scopeStr) {
        //1. 拼装待加密字符串（clientId + scope + 当前精确到毫秒的时间戳）
        String str = clientIdStr + scopeStr + String.valueOf(DateUtils.currentTimeMillis());

        //2. SHA1加密
        String encryptedStr = EncryptUtils.sha1Hex(str);

//        //3.1 保存本次请求的授权范围
//        redisService.setWithExpire(encryptedStr + ":scope", scopeStr, (ExpireEnum.AUTHORIZATION_CODE.getTime()), ExpireEnum.AUTHORIZATION_CODE.getTimeUnit());
//        //3.2 保存本次请求所属的用户信息
//        redisService.setWithExpire(encryptedStr + ":user", user, (ExpireEnum.AUTHORIZATION_CODE.getTime()), ExpireEnum.AUTHORIZATION_CODE.getTimeUnit());

        //4. 返回Authorization Code
        return encryptedStr;
    }

    /**
     * 生成accessToken
     *
     * @param savedClientDetails
     * @param grantType
     * @param expiresIn
     * @return String
     */
    @Override
    public String createAccessToken(AuthClientDetails savedClientDetails, String grantType, Long expiresIn) {
        Date current = new Date();
        Long expiresAt = DateUtils.nextDaysSecond(ExpireEnum.ACCESS_TOKEN.getTime(), null);
        String str = savedClientDetails.getClientId() + String.valueOf(DateUtils.currentTimeMillis());
        String accessTokenStr = "1." + EncryptUtils.sha1Hex(str) + "." + expiresIn + "." + expiresAt;
        AuthAccessToken savedAccessToken = authAccessTokenRepository.findByUserIdAndClientIdAndScope(1
                , savedClientDetails.getId(), "basic");
        if (savedAccessToken != null) {
            savedAccessToken.setAccessToken(accessTokenStr);
            savedAccessToken.setExpiresIn(expiresAt);
            savedAccessToken.setUpdateUser(1);
            savedAccessToken.setUpdateTime(current);
            authAccessTokenRepository.save(savedAccessToken);
        } else {
            savedAccessToken = new AuthAccessToken();
            savedAccessToken.setAccessToken(accessTokenStr);
            savedAccessToken.setUserId(1);
            savedAccessToken.setUserName("wind");
            savedAccessToken.setClientId(savedClientDetails.getId());
            savedAccessToken.setExpiresIn(expiresAt);
            savedAccessToken.setScope("basic");
            savedAccessToken.setGrantType(grantType);
            savedAccessToken.setCreateUser(1);
            savedAccessToken.setUpdateUser(1);
            savedAccessToken.setCreateTime(current);
            savedAccessToken.setUpdateTime(current);
            authAccessTokenRepository.save(savedAccessToken);
        }

        return accessTokenStr;
    }

    /**
     * 生成refreshToken
     *
     * @param authAccessToken
     * @return String
     */
    @Override
    public String createRefreshToken(AuthAccessToken authAccessToken) {
        Date current = new Date();
        Long expiresIn = DateUtils.dayToSecond(ExpireEnum.REFRESH_TOKEN.getTime());
        Long expiresAt = DateUtils.nextDaysSecond(ExpireEnum.REFRESH_TOKEN.getTime(), null);
        String str = authAccessToken.getAccessToken() + String.valueOf(DateUtils.currentTimeMillis());
        String refreshTokenStr = "2." + EncryptUtils.sha1Hex(str) + "." + expiresIn + "." + expiresAt;
        AuthRefreshToken savedRefreshToken = authRefreshTokenRepository.findByTokenId(authAccessToken.getId());
        if (savedRefreshToken != null) {
            savedRefreshToken.setRefreshToken(refreshTokenStr);
            savedRefreshToken.setExpiresIn(expiresAt);
            savedRefreshToken.setUpdateUser(1);
            savedRefreshToken.setUpdateTime(current);
            authRefreshTokenRepository.save(savedRefreshToken);
        } else {
            savedRefreshToken = new AuthRefreshToken();
            savedRefreshToken.setTokenId(authAccessToken.getId());
            savedRefreshToken.setRefreshToken(refreshTokenStr);
            savedRefreshToken.setExpiresIn(expiresAt);
            savedRefreshToken.setCreateUser(1);
            savedRefreshToken.setUpdateUser(1);
            savedRefreshToken.setCreateTime(current);
            savedRefreshToken.setUpdateTime(current);
            authRefreshTokenRepository.save(savedRefreshToken);
        }
        return refreshTokenStr;
    }
}
