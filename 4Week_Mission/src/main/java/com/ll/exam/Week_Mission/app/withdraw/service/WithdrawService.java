package com.ll.exam.Week_Mission.app.withdraw.service;

import com.ll.exam.Week_Mission.app.cash.entity.CashLog;
import com.ll.exam.Week_Mission.app.cash.entity.EventGroup;
import com.ll.exam.Week_Mission.app.cash.entity.PayGroup;
import com.ll.exam.Week_Mission.app.exception.DataNotFoundException;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.service.MemberService;
import com.ll.exam.Week_Mission.app.rebate.entity.RebateOrderItem;
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

        Withdraw withdraw = Withdraw.builder()
                .bankName(bankName)
                .bankAccountNo(bankAccountNo)
                .price(price)
                .accountHolder(accountHolder)
                .member(applicant)
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
        // 1. withdraw 리턴
        Withdraw withdraw = withdrawRepository.findById(withdrawApplyId).get();

        // 2. null 체크
        if(withdraw == null){
            throw new DataNotFoundException("해당 출금 신청 건이 존재하지 않습니다.");
        }

        // 3. cashLog로 예치금 출금 반영
        CashLog withdrawCashLog = memberService.addCash(withdraw.getMember(), -withdraw.getPrice(), EventGroup.WITHDRAW, PayGroup.CASH, null);

        // 4. withdraw 출금여부 set
        withdraw.setWithdrawDone(withdrawCashLog.getId());
    }

    public Withdraw findTop1ByMemberIdOrderByIdDesc(long applicantId) {
        return withdrawRepository.findTop1ByMemberIdOrderByIdDesc(applicantId).orElse(null);
    }
}
