package com.ojt.mockproject.exceptionhandler;

import com.ojt.mockproject.dto.Auth.Responses.ApiResponse;
import com.ojt.mockproject.dto.Order.Responses.GenericResponse;
import com.ojt.mockproject.exceptionhandler.Wallet.WalletAppException;
import com.ojt.mockproject.exceptionhandler.Wallet.WalletException;
import com.ojt.mockproject.exceptionhandler.account.AccountAppException;
import com.ojt.mockproject.exceptionhandler.course.CourseAppException;
import com.ojt.mockproject.exceptionhandler.order.OrderException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExeptionHandler {
    @ExceptionHandler(value = AccountAppException.class)
    ResponseEntity<ApiResponse> handlingAccountAppException(AccountAppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());

        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(value = CourseAppException.class)
    ResponseEntity<ApiResponse> handlingCourseAppException(CourseAppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatus()).body(apiResponse);
    }

    @ExceptionHandler(value = AuthAppException.class)
    ResponseEntity<ApiResponse> handlingAuthAppException(AuthAppException exception) {
        ErrorCode errorCode = exception.getErrorCode();

        ApiResponse apiResponse = new ApiResponse();

        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = WalletAppException.class)
    ResponseEntity<ApiResponse> handlingWalletException(WalletAppException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.badRequest().body(apiResponse);
    }

}
