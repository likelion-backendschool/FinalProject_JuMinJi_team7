package com.ll.exam.Week_Mission.app.post.controller;

import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.member.service.MemberService;
import com.ll.exam.Week_Mission.app.post.dto.request.PostForm;
import com.ll.exam.Week_Mission.app.post.entity.Post;
import com.ll.exam.Week_Mission.app.post.exception.ActorCannotModifyException;
import com.ll.exam.Week_Mission.app.post.hashtag.entity.PostHashTag;
import com.ll.exam.Week_Mission.app.post.hashtag.service.PostHashTagService;
import com.ll.exam.Week_Mission.app.post.keyword.entity.PostKeyword;
import com.ll.exam.Week_Mission.app.post.service.PostService;
import com.ll.exam.Week_Mission.app.security.dto.MemberContext;
import com.ll.exam.Week_Mission.util.Ut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/post")
@Slf4j
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final MemberService memberService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String list(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        List<Post> posts = postService.findAllForPrintByMemberIdOrderByIdDesc(memberContext.getId());

        model.addAttribute("posts", posts);
        model.addAttribute("memberContext", memberContext);

        return "post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, @AuthenticationPrincipal MemberContext memberContext, Model model) {
        Post post = postService.findForPrintById(id).get();

        Member actor = memberContext.getMember();

        if (postService.actorCanModify(actor, post) == false) {
            throw new ActorCannotModifyException("수정 권한이 없습니다.");
        }
        model.addAttribute("post", post);

        return "post/detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String showWrite() {
        return "post/write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(@AuthenticationPrincipal MemberContext memberContext, @Valid PostForm postForm) {
        Member member = memberService.findByUsername(memberContext.getUsername());

        Post post = postService.write(member, postForm.getSubject(), postForm.getContent(), postForm.getContentHtml(), postForm.getPostTagContents());
        String msg = "%d번 게시물이 작성되었습니다.".formatted(post.getId());
        msg = Ut.url.encode(msg);

        return "redirect:/post/%d?msg=%s".formatted(post.getId(), msg);
    }
}
