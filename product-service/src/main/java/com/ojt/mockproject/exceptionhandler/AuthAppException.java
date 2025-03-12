package com.ojt.mockproject.exceptionhandler;

public class AuthAppException extends RuntimeException{
    private ErrorCode errorCode;

    public AuthAppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
