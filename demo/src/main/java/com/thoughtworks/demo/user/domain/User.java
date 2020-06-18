package com.thoughtworks.demo.user.domain;

import javax.persistence.*;

/**
 * 用户信息类
 *
 * @author yunfeng
 * @date 2020/6/8
 */

@Entity
@Table(name = "user")
public class User {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long userId;
    /**
     * userName 用户名
     */
    private String userName;
    /**
     * phoneNumber 电话
     */
    private String phoneNumber;
    /**
     * password 密码
     */
    private String password;
    /**
     * email 邮箱
     */
    private String email;

    public User() {
    }

    public User(String userName, String phoneNumber, String email, String password) {
        this.userName = userName;
        this.phoneNumber=phoneNumber;
        this.email = email;
        this.password = password;

    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +

                '}';
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
