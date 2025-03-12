package com.ojt.mockproject.exceptionhandler.Transaction;

import com.ojt.mockproject.exceptionhandler.ErrorCode;
import lombok.Getter;

@Getter
public class TransactionException extends RuntimeException{
    private final ErrorCode errorCode;

    public TransactionException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}

