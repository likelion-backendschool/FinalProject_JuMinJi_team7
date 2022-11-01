package com.ll.exam.Week_Mission.app.order.entity;

import com.ll.exam.Week_Mission.app.base.entity.BaseEntity;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
@Table(name = "product_order")
public class Order extends BaseEntity {
    String name; // 주문명
    LocalDateTime payDate; // 결제날짜
    boolean readyStatus; // 주문완료여부
    boolean isPaid; // 결제완료여부
    boolean isCanceled; // 취소여부
    boolean isRefunded; // 환불여부

    @ManyToOne(fetch = LAZY)
    private Member buyer;

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = ALL, orphanRemoval = true)
    private List<OrderItem> orderItems = new ArrayList<>();

    public boolean CartAvailable() {
        if ( isCanceled ) return true;
        if ( isRefunded ) return true;

        return false;
    }

    public boolean isPayable() {
        if ( isPaid ) return false;
        if ( isCanceled ) return false;

        return true;
    }

    public boolean isCancellable() {
        if ( isPaid ) return false;
        if ( isCanceled ) return false;
        if ( isRefunded ) return false;

        return true;
    }

    public boolean isRefundable() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        if ( isPaid == false ) return false;
        if ( isCanceled ) return false;
        if ( isRefunded ) return false;
        if( currentDateTime.isAfter(payDate.plusMinutes(10))) return false;

        return true;
    }

    /* 판매 금액 계산 */
    public int calculatePayPrice() {
        int payPrice = 0;

        for (OrderItem orderItem : orderItems) {
            payPrice += orderItem.getSalePrice();
        }
        return payPrice;
    }

    public void setPaymentDone() {
        for (OrderItem orderItem : orderItems) {
            orderItem.setPaymentDone();
        }
        this.isPaid = true;
        this.payDate = LocalDateTime.now();
    }

    /* 결제 금액 계산 */
    public int getPayPrice() {
        int payPrice = 0;
        for (OrderItem orderItem : orderItems) {
            payPrice += orderItem.getPayPrice();
        }

        return payPrice;
    }

    public void setRefundDone() {
        for (OrderItem orderItem : orderItems) {
            orderItem.setRefundDone();
        }
        this.isRefunded = true;
    }
}