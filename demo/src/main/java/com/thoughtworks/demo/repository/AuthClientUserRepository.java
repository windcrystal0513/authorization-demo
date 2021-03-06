package com.thoughtworks.demo.repository;

import com.thoughtworks.demo.domain.AuthClientUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

public interface AuthClientUserRepository extends JpaRepository<AuthClientUser, Long>, JpaSpecificationExecutor<AuthClientUser> {
    /**
     * 指定clientid等信息查询用户信息
     *
     * @param userId 用户id
     * @return AuthClientUser
     */
    @Transactional(rollbackFor = Exception.class)
    AuthClientUser findByAuthClientIdAndUserIdAndAuthScopeId(Integer AuthClientId, Integer userId, Integer AuthScopeId);

}