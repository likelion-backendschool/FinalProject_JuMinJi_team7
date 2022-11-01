package com.ll.exam.Week_Mission.app.cash.entity;

import lombok.Getter;

@Getter
public enum EventGroup {
    ORDER("주문"),
    REFUND("환불"),
    CHARGE("충전"),
    REBATE("정산");;


    EventGroup(String value) {
        this.value = value;
    }

    private String value;

}
