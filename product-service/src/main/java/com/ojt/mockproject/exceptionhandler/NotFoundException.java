package com.ojt.mockproject.exceptionhandler;

public class NotFoundException extends RuntimeException{
    public NotFoundException(String message){
        super(message);
    }
}
