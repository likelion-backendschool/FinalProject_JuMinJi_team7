package com.ll.exam.Week_Mission.app.exception;

public class OrderCannotBeCanceledException extends RuntimeException {
    public OrderCannotBeCanceledException(String message) {
        super(message);
    }
}