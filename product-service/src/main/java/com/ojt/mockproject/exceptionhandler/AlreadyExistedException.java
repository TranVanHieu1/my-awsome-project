package com.ojt.mockproject.exceptionhandler;

public class AlreadyExistedException extends RuntimeException{
    public AlreadyExistedException(String message) {
        super(message);
    }

}

