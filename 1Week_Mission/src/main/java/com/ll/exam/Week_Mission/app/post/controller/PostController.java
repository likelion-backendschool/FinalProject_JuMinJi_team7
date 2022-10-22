package com.ll.exam.Week_Mission.app.post.controller;

import com.ll.exam.Week_Mission.app.post.dto.request.PostForm;
import com.ll.exam.Week_Mission.app.post.entity.Post;
import com.ll.exam.Week_Mission.app.post.hashtag.entity.PostHashTag;
import com.ll.exam.Week_Mission.app.post.hashtag.service.PostHashTagService;
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
import java.util.List;

@Controller
@RequestMapping("/post")
@Slf4j
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final PostHashTagService postHashTagService;

    @GetMapping("/list")
    public String showListWithKeyWord(Model model) {
        List<Post> posts = postService.findAll();

        List<PostHashTag> postHashTags = new ArrayList<>();
        for(Post temp : posts) {
            long postId= temp.getId();

            if(postHashTagService.findByPostId(postId)!=null){
                postHashTags = postHashTagService.findByPostId(postId);
            } else {
                postHashTags = null;
            }
            temp.getExtra().put("postHashTags", postHashTags);

        }

        model.addAttribute("posts", posts);

        return "post/list";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/write")
    public String showWrite() {
        return "post/write";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/write")
    public String write(@AuthenticationPrincipal MemberContext memberContext, @Valid PostForm postForm) {

        Post post = postService.write(memberContext.getId(), postForm.getSubject(), postForm.getContent(), postForm.getContentHtml());

        String msg = "%d번 게시물이 작성되었습니다.".formatted(post.getId());
        msg = Ut.url.encode(msg);

        return "redirect:/post/%d?msg=%s".formatted(post.getId(), msg);
    }

    @GetMapping("/{postId}")
    public String showdetail(@PathVariable long postId) {
        return "post/detail";
    }
}
