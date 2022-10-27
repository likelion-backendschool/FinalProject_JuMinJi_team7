package com.ll.exam.Week_Mission.app.order.repository;

import com.ll.exam.Week_Mission.app.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByBuyerId(long actorId);
}
