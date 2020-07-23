package com.thoughtworks.demo.repository;

import com.thoughtworks.demo.domain.AuthAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

public interface AuthAccessTokenRepository extends JpaRepository<AuthAccessToken, Long>, JpaSpecificationExecutor<AuthAccessToken> {
    /**
     * 指定token等信息查询用户信息
     *
     * @param userId 用户id
     * @param clientId  clientid
     * @param scope 权限范围
     * @return AuthAccessToken
     */
    @Transactional(rollbackFor = Exception.class)
    AuthAccessToken findByUserIdAndClientIdAndScope(Integer userId, Integer clientId, String scope);
    AuthAccessToken findByAccessToken(String accessToken);
    AuthAccessToken findById(Integer Id);

}