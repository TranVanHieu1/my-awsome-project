package com.ojt.mockproject.exceptionhandler.order;

public class HandleOrderPaymentException extends RuntimeException{
    public HandleOrderPaymentException(String message){
        super(message);
    }
}
