package com.ojt.mockproject.exceptionhandler.order;

public class UnableToGetInformationFromExtractedOrder extends RuntimeException{

    public UnableToGetInformationFromExtractedOrder(String message) {
        super(message);
    }

}
