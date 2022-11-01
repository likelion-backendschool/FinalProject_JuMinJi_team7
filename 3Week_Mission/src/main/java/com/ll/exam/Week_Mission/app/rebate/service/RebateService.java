package com.ll.exam.Week_Mission.app.rebate.service;

import com.ll.exam.Week_Mission.app.cash.entity.CashLog;
import com.ll.exam.Week_Mission.app.cash.entity.EventGroup;
import com.ll.exam.Week_Mission.app.cash.entity.PayGroup;
import com.ll.exam.Week_Mission.app.exception.OrderCannotBeRebatedException;
import com.ll.exam.Week_Mission.app.member.service.MemberService;
import com.ll.exam.Week_Mission.app.order.entity.Order;
import com.ll.exam.Week_Mission.app.order.entity.OrderItem;
import com.ll.exam.Week_Mission.app.order.service.OrderService;
import com.ll.exam.Week_Mission.app.rebate.entity.RebateOrderItem;
import com.ll.exam.Week_Mission.app.rebate.repository.RebateOrderItemRepository;
import com.ll.exam.Week_Mission.util.Ut;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Transactional(readOnly = true)
@Service
@RequiredArgsConstructor
public class RebateService {
    private final OrderService orderService;
    private final RebateOrderItemRepository rebateOrderItemRepository;
    private final MemberService memberService;

    @Transactional
    public void makeData(String yearMonth) {
        int monthEndDay = Ut.date.getEndDayOf(yearMonth);

        // 월초
        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        // 월말
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay);
        LocalDateTime fromDate = Ut.date.parse(fromDateStr);
        LocalDateTime toDate = Ut.date.parse(toDateStr);

        // OrderItem 데이터 가져오기
        List<OrderItem> orderItems = orderService.findAllByPayDateBetween(fromDate, toDate);

        // OrderItem -> RebateOrderItem 변환하기
        List<RebateOrderItem> rebateOrderItems = orderItems
                .stream()
                .map(this::orderItemToRebateOrderItem)
                .collect(Collectors.toList());

        // RebateOrderItems -> RebateOrderItem 저장하기
        rebateOrderItems.forEach(this::makeRebateOrderItem);
    }

    public void makeRebateOrderItem(RebateOrderItem item) {
        RebateOrderItem oldRebateOrderItem = rebateOrderItemRepository.findByOrderItemId(item.getOrderItem().getId()).orElse(null);

        if (oldRebateOrderItem != null) {
            rebateOrderItemRepository.delete(oldRebateOrderItem);
        }

        rebateOrderItemRepository.save(item);
    }

    public RebateOrderItem orderItemToRebateOrderItem(OrderItem orderItem) {
        return new RebateOrderItem(orderItem);
    }

    public List<RebateOrderItem> findAllByPayDateBetweenOrderByIdAsc(String yearMonth) {
        int monthEndDay = Ut.date.getEndDayOf(yearMonth);

        String fromDateStr = yearMonth + "-01 00:00:00.000000";
        String toDateStr = yearMonth + "-%02d 23:59:59.999999".formatted(monthEndDay);
        LocalDateTime fromDate = Ut.date.parse(fromDateStr);
        LocalDateTime toDate = Ut.date.parse(toDateStr);

        return rebateOrderItemRepository.findAllByPayDateBetweenOrderByIdAsc(fromDate, toDate);
    }

    @Transactional
    public void rebate(long orderItemId) {
        // 1. rebateOrderItem 리턴
        RebateOrderItem rebateOrderItem = rebateOrderItemRepository.findByOrderItemId(orderItemId).get();

        // 2. 정산가능여부 체크
        if (rebateOrderItem.rebateAvailable() == false) {
            throw new OrderCannotBeRebatedException("정산 불가능한 주문 건입니다.");
        }

        int calculateRebatePrice = rebateOrderItem.calculateRebatePrice();
        Order order = rebateOrderItem.getOrder();

        // 3. 예치금으로 정산
        long cashLogId = memberService.addCash(rebateOrderItem.getProduct().getAuthor(), calculateRebatePrice, EventGroup.REBATE, PayGroup.CASH, order);

        // 4. 정산여부 set
        rebateOrderItem.setRebateDone(cashLogId);
    }
}
