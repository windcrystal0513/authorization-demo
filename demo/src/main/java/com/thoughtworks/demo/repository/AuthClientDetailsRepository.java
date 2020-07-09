package com.thoughtworks.demo.repository;


import com.thoughtworks.demo.domain.AuthClientDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

public interface AuthClientDetailsRepository extends JpaRepository<AuthClientDetails, Long>, JpaSpecificationExecutor<AuthClientDetails> {
    /**
     * 指定clientid查询用户信息
     *

     * @param clientId  clientid

     */
    @Transactional(rollbackFor = Exception.class)
    AuthClientDetails findByClientId(String clientId);
    AuthClientDetails findById(Integer Id);

}