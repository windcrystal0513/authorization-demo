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
@Table(name = "user")
public class User {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    private String username;

    private String password;

    private String mobile;

    private String email;

    private Date createTime;

    private Date updateTime;

    private Integer status;


    public User(String username, String mobile, String email, String password) {
        this.username = username;
        this.mobile=mobile;
        this.email = email;
        this.password = password;

    }

}