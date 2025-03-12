package com.ojt.mockproject.exceptionhandler.order;

public class FailedToPayException extends RuntimeException{

    public FailedToPayException(String message) {
        super(message);
    }

}
