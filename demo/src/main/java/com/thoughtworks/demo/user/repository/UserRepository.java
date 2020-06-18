package com.thoughtworks.demo.user.repository;

import com.thoughtworks.demo.user.domain.User;
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
     * @param userName 用户名
     * @param password 密码
     * @param phoneNumber 电话
     * @param email 邮箱
     * @return User
     */
    @Transactional(rollbackFor = Exception.class)
    User findByUserNameAndPassword(String userName, String password);
    User findByPhoneNumberAndPassword(String phoneNumber, String password);
    User findByEmailAndPassword(String email, String password);
    User findByUserName(String userName);
    User findByPhoneNumber(String phoneNumber);
    User findByEmail(String email);



}
