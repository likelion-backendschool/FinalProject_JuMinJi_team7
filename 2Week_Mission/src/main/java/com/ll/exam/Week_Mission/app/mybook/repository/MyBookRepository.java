package com.ll.exam.Week_Mission.app.mybook.repository;

import com.ll.exam.Week_Mission.app.mybook.entity.MyBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyBookRepository extends JpaRepository<MyBook,Long> {
    List<MyBook> findByMemberId(long memberId);
    Optional<MyBook> findByMemberIdAndProductId(long memberId, long productId);
}
