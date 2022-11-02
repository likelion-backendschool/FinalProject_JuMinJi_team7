package com.ll.exam.Week_Mission.app.exception;

public class ActorCannotBuyTwiceException extends RuntimeException {
    public ActorCannotBuyTwiceException(String message) {
        super(message);
    }
}