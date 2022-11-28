package com.ll.exam.Week_Mission.app.cash.entity;

import lombok.Getter;

@Getter
public enum PayGroup {
    REMITTANCE("무통장입금"),
    CARD("카드"),
    CASH("예치금");

    private String value;

    PayGroup(String value){
        this.value = value;
    }
}
