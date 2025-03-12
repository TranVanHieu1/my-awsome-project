package com.ojt.mockproject.exceptionhandler.Wallet;

import com.ojt.mockproject.exceptionhandler.ErrorCode;

public class WalletAppException extends RuntimeException{
    private ErrorCode errorCode;
    public WalletAppException(ErrorCode errorCode) {
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
