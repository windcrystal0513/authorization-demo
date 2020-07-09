package com.thoughtworks.demo.exception;


public class RegisterException extends RuntimeException{
    private Integer code;
    private String message;
    public RegisterException(Integer code,String message){
        super(message);
        this.message=message;
        this.code=code;
    }
    public RegisterException(String message){
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

