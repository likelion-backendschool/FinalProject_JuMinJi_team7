package com.ll.exam.Week_Mission.app.withdraw.service;

import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.withdraw.entity.Withdraw;
import com.ll.exam.Week_Mission.app.withdraw.repository.WithdrawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@RequiredArgsConstructor
public class WithdrawService {
    private final WithdrawRepository withdrawRepository;

    public void apply(Member applicant, String bankName, int bankAccountNo, int price) {

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

    public void modify(Withdraw oldWithdraw, String bankName, int bankAccountNo, int price) {
        oldWithdraw.setBankName(bankName);
        oldWithdraw.setBankAccountNo(bankAccountNo);
        oldWithdraw.setPrice(price);
    }

}
