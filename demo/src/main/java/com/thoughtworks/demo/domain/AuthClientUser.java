package com.thoughtworks.demo.domain;


import javax.persistence.*;

@Entity
@Table(name = "auth_client_user")
public class AuthClientUser {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Integer id;

    private Integer clientId;

    private Integer userId;

    private Integer scopeId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAuthClientId() {
        return clientId;
    }

    public void setAuthClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getAuthScopeId() {
        return scopeId;
    }

    public void setAuthScopeId(Integer authScopeId) {
        this.scopeId = scopeId;
    }
}