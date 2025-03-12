package com.ojt.mockproject.exceptionhandler.account;

import com.ojt.mockproject.exceptionhandler.ErrorCode;

public class AccountAppException extends RuntimeException {
    private ErrorCode errorCode;

    public AccountAppException(ErrorCode errorCode) {
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
