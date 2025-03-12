package com.ojt.mockproject.exceptionhandler.account;

import com.ojt.mockproject.exceptionhandler.ErrorCode;

public class AccountException extends RuntimeException{
    private ErrorCode errorCode;
    public AccountException(String message) {
        super(message);

    }
    public AccountException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
