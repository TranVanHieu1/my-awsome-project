package com.ojt.mockproject.exceptionhandler.quiz;

public class CannotGetQuizExeption extends RuntimeException{

    public CannotGetQuizExeption(String message) {
        super(message);
    }
}
