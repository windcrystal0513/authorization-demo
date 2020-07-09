package com.thoughtworks.demo.exception;

public class TokenException extends RuntimeException{
    private Integer code;
    private String message;
    public TokenException(Integer code,String message){
        super(message);
        this.message=message;
        this.code=code;
    }
    public TokenException(String message){
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
