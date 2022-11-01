package com.ll.exam.Week_Mission.app.rebate.controller;

import com.ll.exam.Week_Mission.app.rebate.entity.RebateOrderItem;
import com.ll.exam.Week_Mission.app.rebate.service.RebateService;
import com.ll.exam.Week_Mission.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/adm/rebate")
@RequiredArgsConstructor
public class AdminRebateController {
    private final RebateService rebateService;

    @GetMapping("/makeData")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showMakeData() {
        return "adm/rebate/makeData";
    }

    @PostMapping("/makeData")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String makeData(String yearMonth) {
        rebateService.makeData(yearMonth);

        String msg = "정산데이터가 성공적으로 생성되었습니다.";
        return "redirect:/adm/rebate/rebateOrderItemList?yearMonth=" + yearMonth + "&msg=" + msg;
    }

    @GetMapping("/rebateOrderItemList")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String showRebateList(String yearMonth, Model model) {
        if (yearMonth == null) {
            yearMonth = "2022-11";
        }

        List<RebateOrderItem> rebateOrderItems = rebateService.findAllByPayDateBetweenOrderByIdAsc(yearMonth);

        model.addAttribute("items", rebateOrderItems);

        return "adm/rebate/rebateOrderItemList";
    }
}
