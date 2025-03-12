package com.ojt.mockproject.exceptionhandler.token;

public class UnableToExtractTokenForReturnOrder extends RuntimeException{
    public UnableToExtractTokenForReturnOrder(String message) {
        super(message);
    }
}
