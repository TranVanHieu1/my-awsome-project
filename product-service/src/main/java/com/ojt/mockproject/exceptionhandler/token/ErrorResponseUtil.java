package com.ojt.mockproject.exceptionhandler.token;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorResponseUtil {
    public static ResponseEntity<String> createErrorResponse(HttpStatus status, String message) {
        String jsonResponse = String.format("{\"error\": \"%s\"}", message);
        return ResponseEntity.status(status).body(jsonResponse);
    }
}
