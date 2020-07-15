package com.thoughtworks.demo.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "auth_refresh_token")
public class AuthRefreshToken {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    /**
     * 关联的表auth_access_token对应的Access Token记录
     */
    private Integer tokenId;

    /**
     * Refresh Token
     */
    private String refreshToken;

    /**
     * 过期时间戳
     */
    private Long expiresIn;

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;

}