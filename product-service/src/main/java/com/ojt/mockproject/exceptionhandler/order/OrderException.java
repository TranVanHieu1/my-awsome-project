package com.ojt.mockproject.exceptionhandler.order;

import com.ojt.mockproject.exceptionhandler.ErrorCode;

public class OrderException extends RuntimeException {
    private ErrorCode errorCode;

    public OrderException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
