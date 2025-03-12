package com.ojt.mockproject.exceptionhandler.order;

public class OrderExistedException extends RuntimeException{

    public OrderExistedException(String message) {
        super(message);
    }
    
}
