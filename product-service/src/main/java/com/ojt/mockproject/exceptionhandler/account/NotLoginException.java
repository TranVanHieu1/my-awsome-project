package com.ojt.mockproject.exceptionhandler.account;

public class NotLoginException extends RuntimeException{

    public NotLoginException(String message){
        super(message);
    }
}
