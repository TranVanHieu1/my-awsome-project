package com.ojt.mockproject.exceptionhandler.quiz;

import com.ojt.mockproject.exceptionhandler.order.FailedToPayException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

public class QuizExceptionHandler {

    @ExceptionHandler(UnableToSaveQuizException.class)
    public ResponseEntity<?> unableToSaveQuizException(Exception exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CannotGetQuizExeption.class)
    public ResponseEntity<?> cannotGetQuizExeption(Exception exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(UnableToSaveQuestionToQuiz.class)
    public ResponseEntity<?> unableToSaveQuestionToQuiz(Exception exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(UnableToGetInformationFromQuiz.class)
    public ResponseEntity<?> unableToGetInformationFromQuiz(Exception exception) {
        return new ResponseEntity<String>(exception.getMessage(), HttpStatus.NO_CONTENT);
    }

}
