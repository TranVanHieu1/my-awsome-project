package com.ojt.mockproject.exceptionhandler;

public class ValidationException extends RuntimeException {

    private ErrorCode errorCode;

    public ValidationException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}