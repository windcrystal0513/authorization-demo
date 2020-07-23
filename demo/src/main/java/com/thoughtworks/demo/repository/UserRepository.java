package com.thoughtworks.demo.repository;

import com.thoughtworks.demo.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户登录信息数据的数据库操作
 *
 * @author yunfeng
 * @date 2020/06/08
 */
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    /**
     * 指定用户名、密码，查询用户信息
     *
     * @param username 用户名
     * @param password 密码
     * @return User
     */
    @Transactional(rollbackFor = Exception.class)
    User findByUsernameAndPassword(String username, String password);
    User findByMobileAndPassword(String mobile, String password);
    User findByEmailAndPassword(String email, String password);
    User findByUsername(String username);
    User findByMobile(String mobile);
    User findByEmail(String email);
    User findById(Integer id);



}
