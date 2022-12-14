package com.ll.exam.Week_Mission.app.product.controller;

import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.post.entity.Post;
import com.ll.exam.Week_Mission.app.exception.ActorCannotModifyException;
import com.ll.exam.Week_Mission.app.exception.ActorCannotRemoveException;
import com.ll.exam.Week_Mission.app.post.domain.hashtag.entity.PostHashTag;
import com.ll.exam.Week_Mission.app.post.domain.hashtag.service.PostHashTagService;
import com.ll.exam.Week_Mission.app.post.domain.keyword.entity.PostKeyword;
import com.ll.exam.Week_Mission.app.product.domain.tag.entity.ProductTag;
import com.ll.exam.Week_Mission.app.product.dto.request.ProductForm;
import com.ll.exam.Week_Mission.app.product.dto.request.ProductModifyForm;
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

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;
    private final PostHashTagService postHashTagService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String list(Model model, @AuthenticationPrincipal MemberContext memberContext) {
        List<Product> products = productService.findAllForPrintByOrderByIdDesc(memberContext.getMember());

        model.addAttribute("products", products);

        return "product/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Product product = productService.findForPrintById(id).get();

        if (product == null) {
            String errorMsg="%d??? ?????? ????????? ???????????? ????????????".formatted(id);
            return "redirect:/product/list?errorMsg=" + Ut.url.encode(errorMsg);
        }

        List<Post> posts = productService.findPostsByProduct(product);

        model.addAttribute("product", product);
        model.addAttribute("posts", posts);

        return "product/detail";
    }

    @PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
    @GetMapping("/create")
    public String showCreate(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        // 1. ?????? ??????
        Member member = memberContext.getMember();

        // 2. ?????? ????????? ????????? ???????????? ??????
        List <PostHashTag> postHashTags = postHashTagService.findByMemberId(member.getId());

        // 3. ????????? ???????????? ??????????????? ????????? ????????? ???????????? ??????
        List<PostKeyword> postKeywords = new ArrayList<>();
        for(PostHashTag temp: postHashTags) {
            postKeywords.add(temp.getPostkeyword());
        }

        // 4. ????????? ????????? ????????? ?????? ??????
        postKeywords = postKeywords.stream().distinct().toList();

        // 5. ????????? ???????????? ????????? ??? ????????? extra ?????? ??????
        postKeywords.stream().forEach(postkeyword -> {
            postkeyword.getExtra().put("countPostsByKeyword", postHashTagService.findByMemberIdAndPostkeywordId(member.getId(), postkeyword.getId())
                    .stream()
                    .count());
        });

        model.addAttribute("postKeywords", postKeywords);

        return "product/create";
    }

    @PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
    @PostMapping("/create")
    public String create(@AuthenticationPrincipal MemberContext memberContext, @Valid ProductForm productForm) {
        Member author = memberContext.getMember();

        Product product = productService.create(author, productForm.getSubject(), productForm.getPrice(), productForm.getPostKeywordId(), productForm.getProductTagContents());

        String msg = "%d??? ?????? ????????? ?????????????????????.".formatted(product.getId());
        msg = Ut.url.encode(msg);
        return "redirect:/product/%d?msg=%s".formatted(product.getId(), msg);
    }

    @PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
    @GetMapping("/{id}/modify")
    public String showModify(@PathVariable long id, @AuthenticationPrincipal MemberContext memberContext, Model model) {
        Product product = productService.findForPrintById(id).get();

        if (product == null) {
            String errorMsg="%d??? ?????? ????????? ???????????? ????????????".formatted(id);
            return "redirect:/product/list?errorMsg=" + Ut.url.encode(errorMsg);
        }

        Member actor = memberContext.getMember();

        if (productService.actorCanModify(actor, product) == false) {
            throw new ActorCannotModifyException("?????? ?????? ????????? ????????????.");
        }

        model.addAttribute("product", product);

        return "product/modify";
    }

    @PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
    @PostMapping("/{id}/modify")
    public String modify(@PathVariable long id, @AuthenticationPrincipal MemberContext memberContext, @Valid ProductModifyForm productForm) {
        Product product = productService.findById(id).get();
        Member actor = memberContext.getMember();

        if (productService.actorCanModify(actor, product) == false) {
            throw new ActorCannotModifyException("?????? ?????? ????????? ????????????");
        }

        productService.modify(product, productForm.getSubject(), productForm.getPrice(), productForm.getProductTagContents());

        String msg = "%d??? ?????? ????????? ?????????????????????.".formatted(product.getId());
        msg = Ut.url.encode(msg);

        return "redirect:/product/%d?msg=%s".formatted(product.getId(), msg);    }

    @PreAuthorize("isAuthenticated() and hasAuthority('AUTHOR')")
    @PostMapping("/{id}/remove")
    public String remove(@PathVariable long id, @AuthenticationPrincipal MemberContext memberContext) {
        Product product = productService.findById(id).get();

        if (product == null) {
            String errorMsg="%d??? ???????????? ???????????? ????????????".formatted(id);
            return "redirect:/product/list?errorMsg=" + Ut.url.encode(errorMsg);
        }

        Member actor = memberContext.getMember();

        if (productService.actorCanRemove(actor, product) == false) {
            throw new ActorCannotRemoveException("?????? ?????? ????????? ????????????.");
        }

        productService.remove(product);

        String msg = "%d??? ?????? ????????? ?????????????????????.".formatted(product.getId());
        msg = Ut.url.encode(msg);

        return "redirect:/product/list?msg=%s".formatted(product.getId(), msg);
    }

    @GetMapping("/tag/{tagContent}")
    public String tagList(Model model, @PathVariable String tagContent, @AuthenticationPrincipal MemberContext memberContext) {
        List<ProductTag> productTags = productService.getProductTags(tagContent, memberContext.getMember());

        model.addAttribute("productTags", productTags);
        return "product/tagList";
    }
}


