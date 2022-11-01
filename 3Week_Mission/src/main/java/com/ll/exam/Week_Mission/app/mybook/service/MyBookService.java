package com.ll.exam.Week_Mission.app.mybook.service;

import com.ll.exam.Week_Mission.app.exception.ActorCannotAccessException;
import com.ll.exam.Week_Mission.app.exception.ActorCannotBuyTwiceException;
import com.ll.exam.Week_Mission.app.exception.DataNotFoundException;
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

    @Transactional
    public void remove(Member member, Product product) {

        MyBook myBook = findByMemberIdAndProductId(member.getId(), product.getId())
                .orElseThrow(() -> new DataNotFoundException("삭제하려는 도서 상품이 존재하지 않습니다."));

        myBookRepository.delete(myBook);
    }

}
