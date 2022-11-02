package com.ll.exam.Week_Mission.app.withdraw.entity;

import com.ll.exam.Week_Mission.app.base.entity.BaseEntity;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Withdraw extends BaseEntity {
    private String bankName;
    private int bankAccountNo;
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;
}
