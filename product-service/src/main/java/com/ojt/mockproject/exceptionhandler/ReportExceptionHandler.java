package com.ojt.mockproject.exceptionhandler;

public class ReportExceptionHandler extends RuntimeException {
    private ErrorCode errorCode;

    public ReportExceptionHandler(String message, ErrorCode errorCode) {
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
