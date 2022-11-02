package com.ll.exam.Week_Mission.app.withdraw.service;

import com.ll.exam.Week_Mission.app.cash.entity.CashLog;
import com.ll.exam.Week_Mission.app.cash.entity.EventGroup;
import com.ll.exam.Week_Mission.app.cash.entity.PayGroup;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.service.MemberService;
import com.ll.exam.Week_Mission.app.withdraw.entity.Withdraw;
import com.ll.exam.Week_Mission.app.withdraw.repository.WithdrawRepository;
import com.ll.exam.Week_Mission.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class WithdrawService {
    private final WithdrawRepository withdrawRepository;
    private final MemberService memberService;

    public void apply(Member applicant, String bankName, String bankAccountNo, int price, String accountHolder) {

        CashLog withdrawCashLog = memberService.addCash(applicant, -price, EventGroup.WITHDRAW, PayGroup.CASH, null);

        Withdraw withdraw = Withdraw.builder()
                .bankName(bankName)
                .bankAccountNo(bankAccountNo)
                .price(price)
                .accountHolder(accountHolder)
                .member(applicant)
                .withdrawCashLog(withdrawCashLog)
                .build();

        withdrawRepository.save(withdraw);

    }


    public List<Withdraw> findAllByCreateDateBetweenOrderByIdAsc(String yearMonth) {
        int monthEndDay = Ut.date.getEndDayOf(yearMonth);

        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay);
        LocalDateTime fromDate = Ut.date.parse(fromDateStr);
        LocalDateTime toDate = Ut.date.parse(toDateStr);

        return withdrawRepository.findAllByCreateDateBetweenOrderByIdAsc(fromDate, toDate);
    }

    public void withdraw(Long withdrawApplyId) {
    }

    public Withdraw findByMemberId(Long applicantId) {
        return withdrawRepository.findByMemberId(applicantId).orElse(null);
    }
}
