package com.ojt.mockproject.exceptionhandler;

public class TransactionExceptionHandler extends RuntimeException {
    private ErrorCode errorCode;

    public TransactionExceptionHandler(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
