package com.ll.exam.Week_Mission.app.withdraw.controller;

import com.ll.exam.Week_Mission.app.rebate.entity.RebateOrderItem;
import com.ll.exam.Week_Mission.app.rebate.service.RebateService;
import com.ll.exam.Week_Mission.app.withdraw.entity.Withdraw;
import com.ll.exam.Week_Mission.app.withdraw.service.WithdrawService;
import com.ll.exam.Week_Mission.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/adm/withdraw")
@RequiredArgsConstructor
public class AdminWithdrawController {
    private final WithdrawService withdrawService;

    @GetMapping("/applyList")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showWithdrawApplyList(String yearMonth, Model model) {
        if (StringUtils.hasText(yearMonth)) {
            Date now = new Date();
            yearMonth = new SimpleDateFormat("YYYY-MM").format(now.getTime());
        }

        List<Withdraw> withdraws = withdrawService.findAllByCreateDateBetweenOrderByIdAsc(yearMonth);

        model.addAttribute("withdraws", withdraws);

        return "adm/withdraw/applyList";
    }

    @PostMapping("/{WithdrawApplyId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String withdraw(@PathVariable Long WithdrawApplyId) {
        withdrawService.withdraw(WithdrawApplyId);

        return "redirect:/adm/withdraw/applyList?msg=" + Ut.url.encode("%d번 출금신청 출금완료됐습니다.".formatted(WithdrawApplyId));
    }
}
