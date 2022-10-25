package com.ll.exam.Week_Mission.app.post.controller;

import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.post.domain.hashtag.entity.PostHashTag;
import com.ll.exam.Week_Mission.app.post.dto.request.PostForm;
import com.ll.exam.Week_Mission.app.post.entity.Post;
import com.ll.exam.Week_Mission.app.exception.ActorCannotModifyException;
import com.ll.exam.Week_Mission.app.exception.ActorCannotRemoveException;
import com.ll.exam.Week_Mission.app.post.service.PostService;
import com.ll.exam.Week_Mission.app.security.dto.MemberContext;
import com.ll.exam.Week_Mission.util.Ut;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/post")
@Slf4j
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String list(@AuthenticationPrincipal MemberContext memberContext, Model model) {
        Long actorId= memberContext.getId();

        List<Post> posts = postService.findAllForPrintByMemberIdOrderByIdDesc(actorId);

        model.addAttribute("posts", posts);
        model.addAttribute("memberContext", memberContext);

        return "post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, @AuthenticationPrincipal MemberContext memberContext, Model model) {
        Post post = postService.findForPrintById(id).get();

        if (post == null) {
            String errorMsg="%d번 게시글이 존재하지 않습니다".formatted(id);
            return "redirect:/post/list?errorMsg=" + Ut.url.encode(errorMsg);
        }

        Member actor = memberContext.getMember();

        if (postService.actorCanModify(actor, post) == false) {
            throw new ActorCannotModifyException("글 조회 권한이 없습니다.");
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
        Member actor = memberContext.getMember();

        Post post = postService.write(actor, postForm.getSubject(), postForm.getContent(), postForm.getContentHtml(), postForm.getPostTagContents());

        String msg = "%d번 게시물이 작성되었습니다.".formatted(post.getId());
        msg = Ut.url.encode(msg);

        return "redirect:/post/%d?msg=%s".formatted(post.getId(), msg);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}/modify")
    public String showModify(@PathVariable long id, @AuthenticationPrincipal MemberContext memberContext, Model model) {
        Post post = postService.findForPrintById(id).get();

        if (post == null) {
            String errorMsg="%d번 게시글이 존재하지 않습니다".formatted(id);
            return "redirect:/post/list?errorMsg=" + Ut.url.encode(errorMsg);
        }

        Member actor = memberContext.getMember();

        if (postService.actorCanModify(actor, post) == false) {
            throw new ActorCannotModifyException("글 수정 권한이 없습니다.");
        }

        model.addAttribute("post", post);

        return "post/modify";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/modify")
    public String modify(@PathVariable long id, @AuthenticationPrincipal MemberContext memberContext, @Valid PostForm postForm) {
        Post post = postService.findById(id).get();

        if (post == null) {
            String errorMsg="%d번 게시글이 존재하지 않습니다".formatted(id);
            return "redirect:/post/list?errorMsg=" + Ut.url.encode(errorMsg);
        }

        Member actor = memberContext.getMember();

        if (postService.actorCanModify(actor, post) == false) {
            throw new ActorCannotModifyException("글 수정 권한이 없습니다.");
        }

        postService.modify(actor, post, postForm.getSubject(), postForm.getContent(), postForm.getContentHtml(), postForm.getPostTagContents());

        String msg = "%d번 게시물이 수정되었습니다.".formatted(post.getId());
        msg = Ut.url.encode(msg);

        return "redirect:/post/%d?msg=%s".formatted(post.getId(), msg);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/{id}/remove")
    public String remove(@PathVariable long id, @AuthenticationPrincipal MemberContext memberContext) {
        Post post = postService.findById(id).get();

        if (post == null) {
            String errorMsg="%d번 게시글이 존재하지 않습니다".formatted(id);
            return "redirect:/post/list?errorMsg=" + Ut.url.encode(errorMsg);
        }

        Member actor = memberContext.getMember();

        if (postService.actorCanRemove(actor, post) == false) {
            throw new ActorCannotRemoveException("글 삭제 권한이 없습니다.");
        }

        postService.remove(post);

        String msg = "%d번 게시물이 삭제되었습니다.".formatted(post.getId());
        msg = Ut.url.encode(msg);

        return "redirect:/post/list?msg=%s".formatted(post.getId(), msg);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/tag/{tagContent}")
    public String tagList(Model model, @PathVariable String tagContent, @AuthenticationPrincipal MemberContext memberContext) {
        List<PostHashTag> postTags = postService.getPostTags(memberContext.getMember(), tagContent);

        model.addAttribute("postTags", postTags);
        return "post/tagList";
    }
}
