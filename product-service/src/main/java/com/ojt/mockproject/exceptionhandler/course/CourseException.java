package com.ojt.mockproject.exceptionhandler.course;

import com.ojt.mockproject.exceptionhandler.ErrorCode;

public class CourseException extends RuntimeException{
    private final ErrorCode errorCode;

    public CourseException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }



    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
