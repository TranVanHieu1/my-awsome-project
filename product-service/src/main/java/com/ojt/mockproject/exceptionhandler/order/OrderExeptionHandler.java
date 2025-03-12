package com.ojt.mockproject.exceptionhandler.order;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExeptionHandler {

    @ExceptionHandler(FailedToPayException.class)
    public ResponseEntity<?> failedToPayException(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.PAYMENT_REQUIRED);
    }

    @ExceptionHandler(HandleOrderPaymentException.class)
    public ResponseEntity<?> failedToFetchDataFromReturnOrderURL(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(UnableToGetInformationFromExtractedOrder.class)
    public ResponseEntity<?> unableToGetInformationFromExtractedOrder(Exception exception) {
        return new ResponseEntity<>(exception.getMessage(), HttpStatus.NO_CONTENT);
    }

}
