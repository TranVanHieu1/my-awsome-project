package com.ojt.mockproject.exceptionhandler;

public class InvalidToken extends RuntimeException{
    public InvalidToken(String message) {
        super(message);
    }
}
