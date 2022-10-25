package com.ll.exam.Week_Mission.app.cash.repository;

import com.ll.exam.Week_Mission.app.cash.entity.CashLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashLogRepository extends JpaRepository<CashLog, Long> {
}
