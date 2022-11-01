package com.ll.exam.Week_Mission.app.cash.service;

import com.ll.exam.Week_Mission.app.cash.entity.CashLog;
import com.ll.exam.Week_Mission.app.cash.entity.EventGroup;
import com.ll.exam.Week_Mission.app.cash.entity.PayGroup;
import com.ll.exam.Week_Mission.app.cash.repository.CashLogRepository;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.order.entity.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class CashService {
    private final CashLogRepository cashLogRepository;

    public CashLog addCash(Member member, long price, EventGroup eventGroup, PayGroup payGroup, Order order) {
        CashLog cashLog = CashLog.builder()
                .price(price)
                .eventType(eventGroup.getValue() + "_" + payGroup.getValue())
                .order(order)
                .member(member)
                .build();

        cashLogRepository.save(cashLog);

        return cashLog;
    }
}
