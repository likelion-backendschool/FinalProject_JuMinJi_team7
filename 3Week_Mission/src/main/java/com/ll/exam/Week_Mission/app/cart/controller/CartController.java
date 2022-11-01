package com.ll.exam.Week_Mission.app.cart.controller;

import com.ll.exam.Week_Mission.app.cart.entity.CartItem;
import com.ll.exam.Week_Mission.app.cart.service.CartService;
import com.ll.exam.Week_Mission.app.exception.ActorCannotBuyTwiceException;
import com.ll.exam.Week_Mission.app.exception.DataNotFoundException;
import com.ll.exam.Week_Mission.app.exception.PaymentFailedException;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.order.entity.Order;
import com.ll.exam.Week_Mission.app.order.entity.OrderItem;
import com.ll.exam.Week_Mission.app.order.service.OrderService;
import com.ll.exam.Week_Mission.app.product.entity.Product;
import com.ll.exam.Week_Mission.app.product.service.ProductService;
import com.ll.exam.Week_Mission.app.security.dto.MemberContext;
import com.ll.exam.Week_Mission.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Arrays;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/cart")
public class CartController {
    private final CartService cartService;
    private final ProductService productService;
    private final OrderService orderService;

    @GetMapping("/list")
    @PreAuthorize("isAuthenticated()")
    public String showItems(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Member buyer = memberContext.getMember();

        List<CartItem> cartItems = cartService.findByBuyerIdOrderByIdDesc(buyer);

        model.addAttribute("cartItems", cartItems);

        return "cart/list";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add/{productId}")
    public String add(@PathVariable long productId, @AuthenticationPrincipal MemberContext memberContext) {
        Product product= productService.findById(productId).get();

        if(product == null){
            throw new DataNotFoundException("해당 상품은존재하지 않습니다.");
        }

        Member buyer = memberContext.getMember();

        // 주문기록이 있는 상품인지 확인 -> 주문기록이 있으나 취소했거나 환불했을 경우에만 장바구니 담기 가능
        List<Order> existingOrders = orderService.findAllByBuyerId(buyer.getId());
        if(existingOrders != null) {
            for (Order order : existingOrders) {
                if (order.CartAvailable() == false && order.getOrderItems().stream().map(OrderItem::getProduct).anyMatch(p -> p == product)) {
                        throw new ActorCannotBuyTwiceException("기존에 주문한 상품은 장바구니에 담을 수 없습니다. 미결제 상태일 시 주문취소 후 다시 담을 수 있습니다.");
                }
            }
        }

        cartService.addItem(buyer, product);

        return "redirect:/cart/list?msg=" + Ut.url.encode("카트에 상품이 담겼습니다.");
    }

    @PostMapping("/remove")
    @PreAuthorize("isAuthenticated()")
    public String removeItems(String ids, @AuthenticationPrincipal MemberContext memberContext) {
        Member buyer = memberContext.getMember();

        String[] idsArr = ids.split(",");

        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    CartItem cartItem = cartService.findById(id).orElseThrow(()-> new DataNotFoundException("해당 장바구니 품목이 존재하지 않습니다."));

                    if (cartService.actorCanDelete(buyer, cartItem)) {
                        cartService.removeItem(buyer, cartItem.getProduct());
                    }
                });

        return "redirect:/cart/list?msg=" + Ut.url.encode("%d건의 도서 상품이 장바구니에서 삭제완료됐습니다.".formatted(idsArr.length));
    }
}
