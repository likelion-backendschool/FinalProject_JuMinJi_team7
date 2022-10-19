package com.ll.exam.Week_Mission.app.post.service;

import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.post.entity.Post;
import com.ll.exam.Week_Mission.app.post.hashtag.service.PostHashTagService;
import com.ll.exam.Week_Mission.app.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostHashTagService postHashTagService;

    public List<Post> findAll() {
        return postRepository.findAll();
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    public Post write(Long memberId, String subject, String content, String contentHtml) {
        return write(new Member(memberId).getId(), subject, content, contentHtml);
    }

    public Post write(Long memberId, String subject, String content, String contentHtml, String hashTagContents) {
        return write(new Member(memberId), subject, content, contentHtml, hashTagContents);
    }

    public Post write(Member author, String subject, String content, String contentHtml) {
        return write(author, subject, content, contentHtml, "");
    }

    public Post write(Member member, String subject, String content, String contentHtml, String hashTagContents) {
        Post post = Post.builder()
                .member(member)
                .subject(subject)
                .content(content)
                .contentHtml(contentHtml)
                .build();

        postRepository.save(post);

        // HashTag와 HashTagContents(Keyword) 저장 후 HashTag 리턴
        // HashTag는 여러 글에서 사용되므로 기존에 존재한다고 RunTimeException 처리 X
        postHashTagService.applyHashTags(member, post, hashTagContents);

        return post;
    }
}