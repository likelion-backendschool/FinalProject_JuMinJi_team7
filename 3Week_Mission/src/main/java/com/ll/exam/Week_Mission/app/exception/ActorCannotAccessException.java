package com.ll.exam.Week_Mission.app.exception;

public class ActorCannotAccessException extends RuntimeException {
    public ActorCannotAccessException(String message) {
        super(message);
    }
}