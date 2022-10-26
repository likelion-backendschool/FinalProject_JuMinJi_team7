package com.ll.exam.Week_Mission.app.mybook.service;

import com.ll.exam.Week_Mission.app.mybook.entity.MyBook;
import com.ll.exam.Week_Mission.app.mybook.repository.MyBookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyBookService {
    private final MyBookRepository myBookRepository;

    public List<MyBook> findByMemberId(long memberId){
        return myBookRepository.findByMemberId(memberId);
    }

}
