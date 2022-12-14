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
            throw new DataNotFoundException("?????? ????????????????????? ????????????.");
        }

        Member buyer = memberContext.getMember();

        // ??????????????? ?????? ???????????? ?????? -> ??????????????? ????????? ??????????????? ???????????? ???????????? ???????????? ?????? ??????
        List<Order> existingOrders = orderService.findAllByBuyerId(buyer.getId());
        if(existingOrders != null) {
            for (Order order : existingOrders) {
                if (order.cartAvailable() == false && order.getOrderItems().stream().map(OrderItem::getProduct).anyMatch(p -> p == product)) {
                        throw new ActorCannotBuyTwiceException("????????? ????????? ????????? ??????????????? ?????? ??? ????????????. ????????? ????????? ??? ???????????? ??? ?????? ?????? ??? ????????????.");
                }
            }
        }

        cartService.addItem(buyer, product);

        return "redirect:/cart/list?msg=" + Ut.url.encode("????????? ????????? ???????????????.");
    }

    @PostMapping("/remove")
    @PreAuthorize("isAuthenticated()")
    public String removeItems(String ids, @AuthenticationPrincipal MemberContext memberContext) {
        Member buyer = memberContext.getMember();

        String[] idsArr = ids.split(",");

        Arrays.stream(idsArr)
                .mapToLong(Long::parseLong)
                .forEach(id -> {
                    CartItem cartItem = cartService.findById(id).orElseThrow(()-> new DataNotFoundException("?????? ???????????? ????????? ???????????? ????????????."));

                    if (cartService.actorCanDelete(buyer, cartItem)) {
                        cartService.removeItem(buyer, cartItem.getProduct());
                    }
                });

        return "redirect:/cart/list?msg=" + Ut.url.encode("%d?????? ?????? ????????? ?????????????????? ????????????????????????.".formatted(idsArr.length));
    }
}
