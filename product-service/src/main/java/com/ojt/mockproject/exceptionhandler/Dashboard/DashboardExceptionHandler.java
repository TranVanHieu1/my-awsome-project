package com.ojt.mockproject.exceptionhandler.Dashboard;


import com.ojt.mockproject.exceptionhandler.ErrorCode;

public class DashboardExceptionHandler extends RuntimeException {
    private ErrorCode errorCode;

    public DashboardExceptionHandler(String msg, ErrorCode errorCode) {
        super(msg);
        this.errorCode = errorCode;
    }
    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
