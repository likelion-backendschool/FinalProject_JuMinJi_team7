package com.ll.exam.Week_Mission.app.rebate.entity;

import com.ll.exam.Week_Mission.app.base.entity.BaseEntity;
import com.ll.exam.Week_Mission.app.cash.entity.CashLog;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.order.entity.Order;
import com.ll.exam.Week_Mission.app.order.entity.OrderItem;
import com.ll.exam.Week_Mission.app.product.entity.Product;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString(callSuper = true)
public class RebateOrderItem extends BaseEntity {
    private LocalDateTime payDate; // 결제날짜
    private int price; // 권장판매가
    private int salePrice; // 실제판매가
    private int wholesalePrice; // 도매가
    private int pgFee; // 결제대행사 수수료
    private int payPrice; // 결제금액
    private int refundPrice; // 환불금액
    private boolean isPaid; // 결제여부

    // 상품 추가데이터
    private String productSubject;

    // 구매자 추가데이터
    private String buyerName;

    // 판매자 추가데이터
    private String sellerName;

    // CashLog 추가데이터
    private LocalDateTime rebateDate;


    @OneToOne(fetch = LAZY)
    @ToString.Exclude
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OrderItem orderItem;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Product product;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member buyer;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member seller;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Order order;

    @ManyToOne(fetch = LAZY)
    @ToString.Exclude
    @JoinColumn(foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private CashLog rebateCashLog;

    public RebateOrderItem(OrderItem orderItem) {
        this.orderItem = orderItem;
        product = orderItem.getProduct();
        buyer = orderItem.getOrder().getBuyer();
        seller = orderItem.getProduct().getAuthor();
        order = orderItem.getOrder();

        payDate = orderItem.getPayDate();
        price = orderItem.getPrice();
        salePrice = orderItem.getSalePrice();
        wholesalePrice = orderItem.getWholesalePrice();
        pgFee = orderItem.getPgFee();
        payPrice = orderItem.getPayPrice();
        refundPrice = orderItem.getRefundPrice();
        isPaid = orderItem.isPaid();

        // 상품 추가데이터
        productSubject = orderItem.getProduct().getSubject();

        // 구매자 추가데이터
        buyerName = orderItem.getOrder().getBuyer().getUsername();

        // 판매자 추가데이터
        sellerName = orderItem.getProduct().getAuthor().getUsername();
    }

    public int calculateRebatePrice() {
        if (rebateAvailable() == false) {
            return 0;
        }

        return wholesalePrice - pgFee;
    }

    public boolean rebateAvailable() {
        if (refundPrice == payPrice || rebateDate != null) {
            return false;
        }

        return true;
    }

    public void setRebateDone(long cashLogId) {
        rebateDate = LocalDateTime.now();
        this.rebateCashLog = new CashLog(cashLogId);
    }
}
