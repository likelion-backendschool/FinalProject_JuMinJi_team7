package com.ll.exam.Week_Mission.app.withdraw.service;

import com.ll.exam.Week_Mission.app.member.entity.Member;
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

    public void apply(Member applicant, String bankName, String bankAccountNo, int price) {

        Withdraw oldWithdraw = findByMemberId(applicant.getId());

        if(oldWithdraw != null) {
            modify(oldWithdraw, bankName, bankAccountNo, price);
        }

        Withdraw withdraw = Withdraw.builder()
                .bankName(bankName)
                .bankAccountNo(bankAccountNo)
                .price(price)
                .member(applicant)
                .build();

        withdrawRepository.save(withdraw);
    }

    @Transactional(readOnly = true)
    public Withdraw findByMemberId(long applicantId) {
       return withdrawRepository.findByMemberId(applicantId).orElse(null);
    }

    public void modify(Withdraw oldWithdraw, String bankName, String bankAccountNo, int price) {
        oldWithdraw.setBankName(bankName);
        oldWithdraw.setBankAccountNo(bankAccountNo);
        oldWithdraw.setPrice(price);
    }

    public List<Withdraw> findAllByCreateDateBetweenOrderByIdAsc(String yearMonth) {
        int monthEndDay = Ut.date.getEndDayOf(yearMonth);

        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay);
        LocalDateTime fromDate = Ut.date.parse(fromDateStr);
        LocalDateTime toDate = Ut.date.parse(toDateStr);

        return withdrawRepository.findAllByCreateDateBetweenOrderByIdAsc(fromDate, toDate);
    }
}
