package com.ojt.mockproject.exceptionhandler.quiz;

public class UnableToSaveQuestionToQuiz extends RuntimeException{

    public UnableToSaveQuestionToQuiz(String message) {
        super(message);
    }
}
