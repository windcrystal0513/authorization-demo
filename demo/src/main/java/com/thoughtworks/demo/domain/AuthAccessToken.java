package com.thoughtworks.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "auth_access_token")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthAccessToken {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    /**
     * Access Token
     */
    private String accessToken;

    /**
     * 关联的用户ID
     */
    private Integer userId;
    /**
     * 关联的用户名
     */
    private String userName;

    /**
     * 接入的客户端ID
     */
    private Integer clientId;

    /**
     * 过期时间戳
     */
    private Long expiresIn;

    /**
     * 授权类型，比如：authorization_code
     */
    private String grantType;
    /**
     * 可被访问的用户的权限范围，比如：basic、super
     */
    private String scope;

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;
}
