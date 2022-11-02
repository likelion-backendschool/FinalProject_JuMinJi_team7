package com.ll.exam.Week_Mission.app.withdraw.entity;

import com.ll.exam.Week_Mission.app.base.entity.BaseEntity;
import com.ll.exam.Week_Mission.app.cash.entity.CashLog;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.order.entity.OrderItem;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

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
    private String accountHolder;
    private LocalDateTime withdrawDate;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    private CashLog withdrawCashLog;

    public void setWithdrawDone(long cashLogId) {
        withdrawDate = LocalDateTime.now();
        this.withdrawCashLog = new CashLog(cashLogId);
    }

}
