package com.thoughtworks.demo.common;

public class CommonException extends RuntimeException{
    private Integer code;
    private String message;
    public CommonException(Integer code,String message){
        super(message);
        this.message=message;
        this.code=code;
    }
    public CommonException(String message){
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
