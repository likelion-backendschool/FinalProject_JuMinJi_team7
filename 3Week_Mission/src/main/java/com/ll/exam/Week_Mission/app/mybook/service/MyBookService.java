package com.ll.exam.Week_Mission.app.mybook.service;

import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.mybook.entity.MyBook;
import com.ll.exam.Week_Mission.app.mybook.repository.MyBookRepository;
import com.ll.exam.Week_Mission.app.product.entity.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyBookService {
    private final MyBookRepository myBookRepository;

    @Transactional
    public void create(Member member, Product product) {

        findByMemberIdAndProductId(member.getId(), product.getId());

        MyBook myBook = MyBook.builder()
                .member(member)
                .product(product)
                .build();

        myBookRepository.save(myBook);
    }

    public List<MyBook> findByMemberId(long memberId){
        return myBookRepository.findByMemberId(memberId);
    }

    public Optional<MyBook> findByMemberIdAndProductId(long memberId, long productId){
        return myBookRepository.findByMemberIdAndProductId(memberId, productId);
    }

}
