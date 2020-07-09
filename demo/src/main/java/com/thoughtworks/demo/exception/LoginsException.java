package com.thoughtworks.demo.exception;


public class LoginsException extends RuntimeException{
    private Integer code;
    private String message;
    public LoginsException(Integer code,String message){
        super(message);
        this.message=message;
        this.code=code;
    }
    public LoginsException(String message){
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

