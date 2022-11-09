package com.ll.exam.Week_Mission.app.mybook.service;

import com.ll.exam.Week_Mission.app.exception.DataNotFoundException;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.mybook.entity.MyBook;
import com.ll.exam.Week_Mission.app.mybook.repository.MyBookRepository;
import com.ll.exam.Week_Mission.app.order.entity.Order;
import com.ll.exam.Week_Mission.app.post.entity.Post;
import com.ll.exam.Week_Mission.app.product.entity.Product;
import com.ll.exam.Week_Mission.app.product.service.ProductService;
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

    public MyBook findById(Long id) {
        return myBookRepository.findById(id).orElse(null);
    }

    public boolean actorCanSee(Member actor, MyBook myBook) {
        return actor.getId().equals(myBook.getMember().getId());
    }
}
