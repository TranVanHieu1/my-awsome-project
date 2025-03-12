package com.ojt.mockproject.exceptionhandler.Wallet;

import com.ojt.mockproject.exceptionhandler.ErrorCode;
import lombok.Getter;

@Getter
public class WalletLogException extends RuntimeException{
    private ErrorCode errorCode;

    public WalletLogException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
