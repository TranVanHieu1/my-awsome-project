package com.ojt.mockproject.exceptionhandler.account;

public class UnableToSaveAccountException extends RuntimeException {

    public UnableToSaveAccountException(String message) {
        super(message);
    }

}
