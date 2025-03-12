package com.ojt.mockproject.exceptionhandler;

public class BadRequest extends RuntimeException{

    public BadRequest(String message) {
        super(message);
    }
}