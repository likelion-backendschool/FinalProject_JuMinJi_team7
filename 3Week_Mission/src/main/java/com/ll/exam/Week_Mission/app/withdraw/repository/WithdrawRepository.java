package com.ll.exam.Week_Mission.app.withdraw.repository;

import com.ll.exam.Week_Mission.app.withdraw.entity.Withdraw;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WithdrawRepository extends JpaRepository<Withdraw,Long> {
    Optional<Withdraw> findByMemberId(long applicantId);
    List<Withdraw> findAllByCreateDateBetweenOrderByIdAsc(LocalDateTime fromDate, LocalDateTime toDate);
}
