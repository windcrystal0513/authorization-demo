package com.thoughtworks.demo.domain;

import lombok.*;

import javax.persistence.*;
import java.util.Date;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "auth_client_details")
public class AuthClientDetails {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    /**
     * 接入的客户端ID
     */
    private String clientId;

    /**
     * 接入的客户端的名称
     */
    private String clientName;

    /**
     * 接入的客户端的密钥
     */
    private String clientSecret;

    /**
     * 回调地址
     */
    private String redirectUri;

    private String description;

    private Integer createUser;

    private Date createTime;

    private Integer updateUser;

    private Date updateTime;

    private Integer status;

}