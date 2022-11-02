package com.ll.exam.Week_Mission.app.withdraw.entity;

import com.ll.exam.Week_Mission.app.base.entity.BaseEntity;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.order.entity.OrderItem;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class Withdraw extends BaseEntity {
    private String bankName;
    private String bankAccountNo;
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

}
