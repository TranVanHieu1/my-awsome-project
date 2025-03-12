package com.ojt.mockproject.exceptionhandler.course;

import com.ojt.mockproject.exceptionhandler.ErrorCode;
import lombok.Getter;

@Getter
public class CourseAppException extends RuntimeException{
    private ErrorCode errorCode;

    public CourseAppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}


