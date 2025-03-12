package com.ojt.mockproject.exceptionhandler.account;

import com.ojt.mockproject.dto.Auth.Register.Response;
import com.ojt.mockproject.exceptionhandler.BadRequest;
import com.ojt.mockproject.exceptionhandler.InvalidToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AccountAPIHandleException {
    @ExceptionHandler(InvalidToken.class)
    public ResponseEntity invalidToken(InvalidToken invalidToken) {
        Response response = new Response(400, invalidToken.getMessage(), null);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(BadRequest.class)
    public ResponseEntity badRequest(BadRequest badRequest) {
        Response response = new Response(400, badRequest.getMessage(), null);
        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(UnableToSaveAccountException.class)
    public ResponseEntity<?> unableToSaveAccount(Exception exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }


    @ExceptionHandler(NotLoginException.class)
    public ResponseEntity<?> notLogin(Exception exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NON_AUTHORITATIVE_INFORMATION);
    }
}
