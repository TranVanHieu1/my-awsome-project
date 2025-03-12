package com.ojt.mockproject.exceptionhandler;

import com.ojt.mockproject.exceptionhandler.account.AccountException;
import com.ojt.mockproject.exceptionhandler.course.CourseAppException;
import com.ojt.mockproject.exceptionhandler.course.CourseException;
import com.ojt.mockproject.exceptionhandler.order.OrderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handlerNotFoundException(NotFoundException ex, WebRequest req) {
        return new ErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    //Goi Class Exception da tao
    @ExceptionHandler(AlreadyExistedException.class)
    //Tra ve response Status ALREADY_REPORTED
    @ResponseStatus(HttpStatus.ALREADY_REPORTED)
    public ErrorResponse handlerAlreadyExistedException(AlreadyExistedException ex, WebRequest req) {
        return new ErrorResponse(HttpStatus.ALREADY_REPORTED, ex.getMessage());
    }

    @ExceptionHandler(OrderException.class)
    public ErrorResponse handlerOrderException(AlreadyExistedException ex, WebRequest req) {
        return new ErrorResponse(HttpStatus.ALREADY_REPORTED, ex.getMessage());
    }

    @ExceptionHandler(AccountException.class)
    public ErrorResponse handlerAccountException(AlreadyExistedException ex, WebRequest req) {
        return new ErrorResponse(HttpStatus.ALREADY_REPORTED, ex.getMessage());
    }
    @ExceptionHandler(ValidationException.class)
    public ErrorResponse handlerValidationException(AlreadyExistedException ex, WebRequest req) {
        return new ErrorResponse(HttpStatus.ALREADY_REPORTED, ex.getMessage());
    }

    @ExceptionHandler(CourseException.class)
    public ErrorResponse handlerCourseException(AlreadyExistedException ex, WebRequest req) {
        return new ErrorResponse(HttpStatus.ALREADY_REPORTED, ex.getMessage());
    }



    @ExceptionHandler(CourseAppException.class)
    public ResponseEntity<?> unableToSave(Exception exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


}
