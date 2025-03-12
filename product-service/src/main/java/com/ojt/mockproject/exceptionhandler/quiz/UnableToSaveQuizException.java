package com.ojt.mockproject.exceptionhandler.quiz;

import com.ojt.mockproject.exceptionhandler.course.UnableToSaveCourseException;

public class UnableToSaveQuizException extends RuntimeException {
    public UnableToSaveQuizException(String message) {
        super(message);
    }
}
