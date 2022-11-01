package com.ll.exam.Week_Mission.app.rebate.service;

import com.ll.exam.Week_Mission.app.order.entity.OrderItem;
import com.ll.exam.Week_Mission.app.order.service.OrderService;
import com.ll.exam.Week_Mission.app.rebate.entity.RebateOrderItem;
import com.ll.exam.Week_Mission.app.rebate.repository.RebateOrderItemRepository;
import com.ll.exam.Week_Mission.util.Ut;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RebateService {
    private final OrderService orderService;
    private final RebateOrderItemRepository rebateOrderItemRepository;

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
}
