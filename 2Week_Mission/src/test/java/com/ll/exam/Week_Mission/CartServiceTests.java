package com.ll.exam.Week_Mission;

import com.ll.exam.Week_Mission.app.cart.entity.CartItem;
import com.ll.exam.Week_Mission.app.cart.service.CartService;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.repository.MemberRepository;
import com.ll.exam.Week_Mission.app.product.entity.Product;
import com.ll.exam.Week_Mission.app.product.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Rollback(false)
@SpringBootTest
@Transactional
@ActiveProfiles("test")
public class CartServiceTests {
    @Autowired
    private ProductService productService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CartService cartService;

    @Test
    @DisplayName("장바구니에 담기")
    void t1() {
        Member buyer = memberRepository.findByUsername("user1").get();

        Product product2 = productService.findById(2).get();
        Product product4 = productService.findById(4).get();

        CartItem cartItem2 = cartService.addItem(buyer, product2);
        CartItem cartItem4 = cartService.addItem(buyer, product4);

        assertThat(cartItem2).isNotNull();
        assertThat(cartItem4).isNotNull();
        //assertThat(cartItem4).isNull(); // expected: false
    }

    @Test
    @DisplayName("장바구니에서 제거")
    void t2() {
        Member buyer1 = memberRepository.findByUsername("user1").get();

        Product product2 = productService.findById(2).get();
        Product product4 = productService.findById(4).get();

        cartService.removeItem(buyer1, product2);
        cartService.removeItem(buyer1, product4);

        assertThat(cartService.hasItem(buyer1, product2)).isFalse();
        assertThat(cartService.hasItem(buyer1, product4)).isFalse();
    }
}
