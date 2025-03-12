package com.ojt.mockproject.exceptionhandler;

import com.ojt.mockproject.exceptionhandler.account.NotLoginException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiHanleException {
    @ExceptionHandler(AlreadyExistedException.class)
    public ResponseEntity<?> alreadyExist(AlreadyExistedException exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.ALREADY_REPORTED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> alreadyExist(Exception exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
