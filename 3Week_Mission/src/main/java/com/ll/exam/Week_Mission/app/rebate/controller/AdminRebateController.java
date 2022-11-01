package com.ll.exam.Week_Mission.app.rebate.controller;

import com.ll.exam.Week_Mission.app.rebate.entity.RebateOrderItem;
import com.ll.exam.Week_Mission.app.rebate.service.RebateService;
import com.ll.exam.Week_Mission.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.http.HttpRequest;
import java.util.Arrays;
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

    @PostMapping("/rebateOne/{rebateOrderItemId}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String rebateOne(@PathVariable long rebateOrderItemId) { // Todo: 이전 rebateOrderItemList 페이지에서 파라미터(yearMonth) 가져와 redirect 하도록 리팩토링
         rebateService.rebate(rebateOrderItemId);

        return "redirect:/adm/rebate/rebateOrderItemList?msg=" + Ut.url.encode("%d번 상품이 정산완료됐습니다.".formatted(rebateOrderItemId));
    }

    @PostMapping("/rebate")
    @PreAuthorize("hasAuthority('ADMIN')")
    public String rebate(String ids) {

        String[] idsArr = ids.split(",");

        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    rebateService.rebate(id);
                });

        return "redirect:/adm/rebate/rebateOrderItemList?msg=" + Ut.url.encode("%d건의 품목이 정산처리되었습니다.".formatted(idsArr.length));
    }
}
