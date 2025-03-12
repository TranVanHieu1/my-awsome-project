package com.ojt.mockproject.exceptionhandler.course;

public class UnableToSaveCourseException extends RuntimeException{
    public UnableToSaveCourseException(String message) {
        super(message);
    }
}
