package com.ojt.mockproject.exceptionhandler.Report;

import com.ojt.mockproject.exceptionhandler.ErrorCode;
import lombok.Getter;

@Getter
public class ReportException extends RuntimeException{
    private final ErrorCode errorCode;

    public ReportException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

}
