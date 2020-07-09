package com.thoughtworks.demo.repository;

import com.thoughtworks.demo.domain.AuthScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

public interface AuthScopeRepository extends JpaRepository<AuthScope, Long>, JpaSpecificationExecutor<AuthScope> {
    /**
     * 指定权限范围查询用户信息
     *
     * @param scopeName 权限范围
     * @return AuthScope
     */
    @Transactional(rollbackFor = Exception.class)
    AuthScope findByScopeName(String scopeName);

}