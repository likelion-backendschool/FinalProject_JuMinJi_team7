package com.ll.exam.Week_Mission.app.exception;

public class OrderCannotBeRebatedException extends RuntimeException {
    public OrderCannotBeRebatedException(String message) {
        super(message);
    }
}