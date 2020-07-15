package com.thoughtworks.demo.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "auth_scope")
public class AuthScope {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id

    private Integer id;
    /**
     * 可被访问的用户的权限范围，比如：basic、super
     */
    private String scopeName;

    private String description;

}