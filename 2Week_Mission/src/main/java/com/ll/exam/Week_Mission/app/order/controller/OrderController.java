package com.ll.exam.Week_Mission.app.order.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ll.exam.Week_Mission.app.exception.ActorCannotAccessException;
import com.ll.exam.Week_Mission.app.exception.OrderCannotBeCanceledException;
import com.ll.exam.Week_Mission.app.exception.OrderCannotBeRefundedException;
import com.ll.exam.Week_Mission.app.exception.PaymentFailedException;
import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.service.MemberService;
import com.ll.exam.Week_Mission.app.order.entity.Order;
import com.ll.exam.Week_Mission.app.order.service.OrderService;
import com.ll.exam.Week_Mission.app.security.dto.MemberContext;
import com.ll.exam.Week_Mission.util.Ut;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;
    private final MemberService memberService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;
    @Value("${spring.custom.toss-payments.secret-key}")
    private String SECRET_KEY;

    /* order list */
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String list(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Long actorId= memberContext.getId();

        List<Order> orders = orderService.findAllByBuyerId(actorId);

        model.addAttribute("orders", orders);

        return "order/list";
    }

    /* order detail */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showDetail(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id, Model model) {
        Order order = orderService.findById(id);

        Member actor = memberService.findByUsername(memberContext.getUsername());

        long restCash = actor.getRestCash();

        if (orderService.actorCanSee(actor, order) == false) {
            throw new ActorCannotAccessException("?????? ????????? ?????? ????????? ????????????.");
        }

        model.addAttribute("order", order);
        model.addAttribute("restCash", restCash);

        return "order/detail";
    }

    /* payment only with restCash detail */
    @GetMapping("/{id}/payByRestCashOnly")
    @PreAuthorize("isAuthenticated()")
    public String showDetailFullPaymentWithRestCash(@AuthenticationPrincipal MemberContext memberContext, @PathVariable long id) {
        Order order = orderService.findById(id);

        Member actor = memberContext.getMember();

        if (orderService.actorCanPayment(actor, order) == false) {
            throw new ActorCannotAccessException("?????? ????????? ?????? ????????? ????????????.");
        }

        orderService.payByRestCashOnly(order);

        return "redirect:/order/%d?msg=%s".formatted(order.getId(), Ut.url.encode("??????????????? ?????? ???????????????????????????."));
    }

    /* create order */
    @PostMapping("/create")
    @PreAuthorize("isAuthenticated()")
    public String createOrder(@AuthenticationPrincipal MemberContext memberContext) {
        Member member = memberContext.getMember();
        Order order = orderService.createFromCart(member);

        return "redirect:/order/%d".formatted(order.getId()) + "?msg=" + Ut.url.encode("%d??? ????????? ?????????????????????.".formatted(order.getId()));
    }

    /* toss-payments handler */
    @PostConstruct
    private void init() {
        restTemplate.setErrorHandler(new ResponseErrorHandler() {
            @Override
            public boolean hasError(ClientHttpResponse response) {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) {
            }
        });
    }

    /* toss-payments success */
    @RequestMapping("/{id}/success")
    public String confirmPayment(
            @PathVariable long id,
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Long amount,
            @AuthenticationPrincipal MemberContext memberContext,
            Model model
    ) throws Exception {

        Order order = orderService.findById(id);

        long orderIdInputed = Long.parseLong(orderId.split("__")[1]);

        if ( id != orderIdInputed ) {
            throw new PaymentFailedException("????????? ??????????????? ??????????????? ?????? ??????????????? ???????????? ????????????.");
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + Base64.getEncoder().encodeToString((SECRET_KEY + ":").getBytes()));
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> payloadMap = new HashMap<>();
        payloadMap.put("orderId", orderId);
        payloadMap.put("amount", String.valueOf(amount));

        // ??????????????? ????????? ??????
        Member actor = memberService.findByUsername(memberContext.getUsername());
        long restCash = actor.getRestCash();
        long payPriceRestCash = order.calculatePayPrice() - amount;

        if (payPriceRestCash > restCash) {
            throw new PaymentFailedException("?????? ???????????? ???????????????.");
        }

        HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(payloadMap), headers);

        ResponseEntity<JsonNode> responseEntity = restTemplate.postForEntity(
                "https://api.tosspayments.com/v1/payments/" + paymentKey, request, JsonNode.class);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            // ?????? ???????????? ??????
            orderService.payByTossPayments(order, payPriceRestCash);

            model.addAttribute("orderName", order.getName());

            return "order/success";
        } else {
            JsonNode failNode = responseEntity.getBody();
            model.addAttribute("message", failNode.get("message").asText());
            model.addAttribute("code", failNode.get("code").asText());
            return "order/fail";
        }
    }

    /* toss-payments fail */
    @RequestMapping("/{id}/fail")
    public String failPayment(@RequestParam String message, @RequestParam String code, Model model) {
        model.addAttribute("message", message);
        model.addAttribute("code", code);
        return "order/fail";
    }

    /* cancel order */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public String cancel(@PathVariable long id, @AuthenticationPrincipal MemberContext memberContext) {
        Order order = orderService.findById(id);

        if(order.isCancellable()) {
            throw new OrderCannotBeCanceledException("?????? ????????? ?????? ??????????????????.");
        }

        Member actor = memberContext.getMember();

        if (orderService.actorCanSee(actor, order) == false) {
            throw new ActorCannotAccessException("?????? ????????? ?????? ????????? ????????????.");
        }

        orderService.cancel(order);

        return "redirect:/order/%d?msg=%s".formatted(order.getId(), Ut.url.encode("%d??? ????????? ?????????????????????."));
    }

    /* refund order */
    @PostMapping("/{id}/refund")
    @PreAuthorize("isAuthenticated()")
    public String refund(@PathVariable long id, @AuthenticationPrincipal MemberContext memberContext) {
        Order order = orderService.findById(id);

        if(order.isRefundable() == false) {
            throw new OrderCannotBeRefundedException("?????? ????????? ?????? ??????????????????.");
        }

        Member actor = memberContext.getMember();

        if (orderService.actorCanSee(actor, order) == false) {
            throw new ActorCannotAccessException("?????? ????????? ?????? ????????? ????????????.");
        }

        orderService.refund(order);

        return "redirect:/order/%d?msg=%s".formatted(order.getId(), Ut.url.encode("%d??? ????????? ?????????????????????."));
    }

}
