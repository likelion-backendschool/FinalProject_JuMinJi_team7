package com.ll.exam.Week_Mission.app.base.initData;

import com.ll.exam.Week_Mission.app.cart.entity.CartItem;
import com.ll.exam.Week_Mission.app.cart.service.CartService;
import com.ll.exam.Week_Mission.app.cash.entity.EventGroup;
import com.ll.exam.Week_Mission.app.cash.entity.PayGroup;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.service.MemberService;
import com.ll.exam.Week_Mission.app.order.entity.Order;
import com.ll.exam.Week_Mission.app.order.service.OrderService;
import com.ll.exam.Week_Mission.app.post.domain.keyword.service.PostKeywordService;
import com.ll.exam.Week_Mission.app.post.service.PostService;
import com.ll.exam.Week_Mission.app.product.entity.Product;
import com.ll.exam.Week_Mission.app.product.service.ProductService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "test"})
public class BaseInitData {
    private boolean initDataDone = false;

    @Bean
    CommandLineRunner initData(
            MemberService memberService,
            PostService postService,
            ProductService productService,
            PostKeywordService postKeywordService,
            CartService cartService,
            OrderService orderService
    ) {
        return args -> {
            if (initDataDone) {
                return;
            }

            initDataDone = true;
            Member adm = memberService.join("adm", "12341234", "adm@meotbooks.com", "adm");
            Member member1 = memberService.join("user1", "12341234", "user1@meotbooks.com", "user1Author");
            Member member2 = memberService.join("user2", "12341234", "user2@meotbooks.com", "user2Author");
            Member member3 = memberService.join("user3", "12341234", "user3@meotbooks.com", null);

            postService.write(
                    member1,
                    "자바를 우아하게 사용하는 방법",
                    "# 내용 1",
                    "<h1>내용 1</h1>",
                    "#IT #자바 #카프카"
            );

            postService.write(
                    member1,
                    "자바스크립트를 우아하게 사용하는 방법",
                    """
                            # 자바스크립트는 이렇게 쓰세요.
                                                    
                            ```js
                            const a = 10;
                            console.log(a);
                            ```
                            """.stripIndent(),
                    """
                            <h1>자바스크립트는 이렇게 쓰세요.</h1>
                                                    """.stripIndent(),
                    "#IT #프론트엔드 #리액트"
            );

            postService.write(member2, "제목 3", "내용 3", "내용 3", "#IT# 프론트엔드 #HTML #CSS");
            postService.write(member2, "제목 4", "내용 4", "내용 4", "#IT #스프링부트 #자바");
            postService.write(member1, "제목 5", "내용 5", "내용 5", "#IT #자바 #카프카");
            postService.write(member1, "제목 6", "내용 6", "내용 6", "#IT #프론트엔드 #REACT");
            postService.write(member2, "제목 7", "내용 7", "내용 7", "#IT# 프론트엔드 #HTML #CSS");
            postService.write(member2, "제목 8", "내용 8", "내용 8", "#IT #스프링부트 #자바");

            Product product1 = productService.create(member1, "상품명1", 30_000, postKeywordService.findIdByContent("카프카"), "#IT #카프카");
            Product product2 = productService.create(member2, "상품명2", 40_000, postKeywordService.findIdByContent("스프링부트"), "#IT #REACT");
            Product product3 = productService.create(member1, "상품명3", 50_000, postKeywordService.findIdByContent("REACT"), "#IT #REACT");
            Product product4 = productService.create(member2, "상품명4", 60_000, postKeywordService.findIdByContent("HTML"), "#IT #HTML");

            // CartServiceTests 시 @Rollback(false)로 DB 기록을 제대로 확인하려면 해당 부분 주석처리 필요
            CartItem cartItem1 = cartService.addItem(member1, product2);
            CartItem cartItem2 = cartService.addItem(member1, product4);
            CartItem cartItem3 = cartService.addItem(member2, product1);

            memberService.addCash(member1, 10_000, EventGroup.CHARGE, PayGroup.REMITTANCE, null);
            memberService.addCash(member1, 20_000, EventGroup.CHARGE, PayGroup.REMITTANCE, null);
            memberService.addCash(member1, 1_000_000, EventGroup.CHARGE, PayGroup.REMITTANCE, null);

            memberService.addCash(member2, 2_000_000, EventGroup.CHARGE, PayGroup.REMITTANCE, null);

            // 1번 주문 : 결제완료
            Order order1 = orderService.createFromCart(member1);

            int order1PayPrice = order1.calculatePayPrice();
            orderService.payByRestCashOnly(order1);

            // 2번 주문 : 결제 후 환불
            Order order2 = orderService.createFromCart(member2);

            orderService.payByRestCashOnly(order2);

            orderService.refund(order2);

            // 3번 주문 : 결제 전
            CartItem cartItem4 = cartService.addItem(member2, product3);
            CartItem cartItem5 = cartService.addItem(member2, product1);

            Order order3 = orderService.createFromCart(member2);

            //
            cartService.addItem(member1, product2);
            cartService.addItem(member1, product4);

            cartService.addItem(member2, product1);
            cartService.addItem(member2, product3);
        };
    }
}
