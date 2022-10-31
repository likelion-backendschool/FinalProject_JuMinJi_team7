package com.ll.exam.Week_Mission.app.order.repository;

import com.ll.exam.Week_Mission.app.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

}
