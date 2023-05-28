package com.quiz.api.exception;

import lombok.Data;

@Data
public class CustomException extends RuntimeException {
    private int statusCode;
    private String errorMessage;
    public CustomException(int statusCode,String errorMessage){
        super(errorMessage);
        this.statusCode=statusCode;
        this.errorMessage=errorMessage;
    }

}
