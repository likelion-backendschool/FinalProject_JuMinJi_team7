package com.ll.exam.Week_Mission.app.mybook.controller;

import com.ll.exam.Week_Mission.app.base.dto.RsData;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.mybook.entity.MyBook;
import com.ll.exam.Week_Mission.app.mybook.service.MyBookService;
import com.ll.exam.Week_Mission.app.post.entity.Post;
import com.ll.exam.Week_Mission.app.product.entity.Product;
import com.ll.exam.Week_Mission.app.product.service.ProductService;
import com.ll.exam.Week_Mission.app.security.dto.MemberContext;
import com.ll.exam.Week_Mission.util.Ut;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "ApiMyBookController", description = "내 도서 리스트 기능과 도서 상세정보 구현 기능 담당 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/myBooks")
public class ApiMyBookController {
    private final MyBookService myBookService;
    private final ProductService productService;

    @GetMapping("")
    public ResponseEntity<RsData> showMyBookList(@Parameter(hidden = true) @AuthenticationPrincipal MemberContext memberContext){
        Member member = memberContext.getMember();

        if(member == null){
            return Ut.spring.responseEntityOf(RsData.of("F-2", "일치하는 회원이 존재하지 않습니다."));
        }

        List<MyBook> myBooks = myBookService.findByMemberId(member.getId());

        return Ut.spring.responseEntityOf(
                RsData.successOf(
                        Ut.mapOf(
                                "myBooks", myBooks
                        )
                )
        );
    }

    @GetMapping("{id}")
    public ResponseEntity<RsData> showDetail(@Parameter(hidden = true) @PathVariable Long id,
                                             @Parameter(hidden = true) @AuthenticationPrincipal MemberContext memberContext) {
        Member member = memberContext.getMember();

        if(member == null){
            return Ut.spring.responseEntityOf(RsData.of("F-2", "일치하는 회원이 존재하지 않습니다."));
        }

        MyBook myBook = myBookService.findById(id);

        long ownerId = myBook.getMember().getId();

        // fixme: product 객체 자체로 전달 (problem->jackson.databind.exc.InvalidDefinitionException)
        // Product product = myBook.getProduct();
        long productId= myBook.getProduct().getId();
        String productSubject = myBook.getProduct().getSubject();
        String productAuthorName = myBook.getProduct().getAuthor().getUsername();

        // fixme: Post 리스트 자체로 전달 (problem->jackson.databind.exc.InvalidDefinitionException)
        List<String> bookChaptersSubject = productService.findPostsByProduct(myBook.getProduct()).stream().map(Post::getSubject).collect(Collectors.toList());

        if (myBook == null) {
            return Ut.spring.responseEntityOf(RsData.of("F-1", "해당 도서 상품은 존재하지 않습니다."));
        }

        if(myBookService.actorCanSee(member,myBook) == false) {
            return Ut.spring.responseEntityOf(RsData.of("F-4", "해당 회원은 도서 상품을 조회할 권한이 없습니다."));
        }

        return Ut.spring.responseEntityOf(
                RsData.successOf(
                        Ut.mapOf(
                                "myBook", myBook,
                                "ownerId", ownerId,
                                "productId", productId,
                                "productSubject", productSubject,
                                "productAuthorName", productAuthorName,
                                "bookChaptersSubject", bookChaptersSubject
                        )
                )
        );
    }
}
