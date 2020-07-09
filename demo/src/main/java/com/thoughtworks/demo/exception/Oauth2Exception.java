package com.thoughtworks.demo.exception;


public class Oauth2Exception extends RuntimeException{
    private Integer code;
    private String message;
    public Oauth2Exception(Integer code, String message){
        super(message);
        this.message=message;
        this.code=code;
    }
    public Oauth2Exception(String message){
        super(message);
        this.message=message;
    }
    public Integer getCode(){
        return code;
    }
    public String getMsg(){
        return message;
    }

}

