package com.thoughtworks.demo.repository;

import com.thoughtworks.demo.domain.AuthRefreshToken;
import com.thoughtworks.demo.domain.AuthRefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

public interface AuthRefreshTokenRepository extends JpaRepository<AuthRefreshToken, Long>, JpaSpecificationExecutor<AuthRefreshToken> {
    /**
     * 指定token等信息查询用户信息
     *
     * @param tokenId tokenid
     * @param refreshToken
     * @return AuthRefreshToken
     */
    @Transactional(rollbackFor = Exception.class)
    AuthRefreshToken findByTokenId(Integer tokenId);
    AuthRefreshToken findByRefreshToken(String refreshToken);
}