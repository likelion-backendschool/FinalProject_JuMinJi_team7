package com.ll.exam.Week_Mission.app.exception;

public class ActorCannotModifyException extends RuntimeException {
    public ActorCannotModifyException(String message) {
        super(message);
    }
}