package com.ll.exam.Week_Mission.app.cash.service;

import com.ll.exam.Week_Mission.app.cash.entity.CashLog;
import com.ll.exam.Week_Mission.app.cash.repository.CashLogRepository;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class CashService {
    private final CashLogRepository cashLogRepository;

    public CashLog addCash(Member member, long price, String eventType) {
        CashLog cashLog = CashLog.builder()
                .price(price)
                .eventType(eventType)
                .member(member)
                .build();

        cashLogRepository.save(cashLog);

        return cashLog;
    }
}
