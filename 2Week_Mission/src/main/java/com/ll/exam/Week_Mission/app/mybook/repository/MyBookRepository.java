package com.ll.exam.Week_Mission.app.mybook.repository;

import com.ll.exam.Week_Mission.app.mybook.entity.MyBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyBookRepository extends JpaRepository<MyBook,Long> {
    List<MyBook> findByMemberId(long memberId);
}
