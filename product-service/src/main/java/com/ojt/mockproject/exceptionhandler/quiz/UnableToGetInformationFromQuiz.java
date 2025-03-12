package com.ojt.mockproject.exceptionhandler.quiz;

public class UnableToGetInformationFromQuiz extends RuntimeException{

    public UnableToGetInformationFromQuiz(String message) {
        super(message);
    }
}
