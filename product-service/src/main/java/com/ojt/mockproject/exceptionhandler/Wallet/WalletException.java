package com.ojt.mockproject.exceptionhandler.Wallet;

import com.ojt.mockproject.exceptionhandler.ErrorCode;

public class WalletException extends RuntimeException {
    private ErrorCode errorCode;

    public WalletException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
