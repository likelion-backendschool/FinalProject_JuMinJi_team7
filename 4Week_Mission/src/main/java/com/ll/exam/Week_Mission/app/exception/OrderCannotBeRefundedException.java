package com.ll.exam.Week_Mission.app.exception;

public class OrderCannotBeRefundedException extends RuntimeException {
    public OrderCannotBeRefundedException(String message) {
        super(message);
    }
}