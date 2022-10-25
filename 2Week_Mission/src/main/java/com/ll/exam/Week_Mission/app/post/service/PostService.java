package com.ll.exam.Week_Mission.app.post.service;

import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.post.entity.Post;
import com.ll.exam.Week_Mission.app.post.domain.hashtag.entity.PostHashTag;
import com.ll.exam.Week_Mission.app.post.domain.hashtag.service.PostHashTagService;
import com.ll.exam.Week_Mission.app.post.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostHashTagService postHashTagService;

    public List<Post> findAllForPrintByMemberIdOrderByIdDesc(long authorId) {
        List<Post> posts = postRepository.findAllByMemberIdOrderByIdDesc(authorId);
        loadForPrintData(posts);

        return posts;
    }

    public void loadForPrintData(List<Post> posts) {
        long[] ids = posts
                .stream()
                .mapToLong(Post::getId)
                .toArray();

        List<PostHashTag> postTagsByPostIds = postHashTagService.getPostTagsByPostIdIn(ids);

        Map<Long, List<PostHashTag>> postTagsByPostIdsMap = postTagsByPostIds.stream()
                .collect(groupingBy(
                        postTag -> postTag.getPost().getId(), toList()
                ));

        posts.stream().forEach(post -> {
            List<PostHashTag> postTags = postTagsByPostIdsMap.get(post.getId());

            if (postTags == null || postTags.size() == 0) return;

            post.getExtra().put("postTags", postTags);
        });
    }

    public Optional<Post> findForPrintById(long id) {
        Optional<Post> opPost = findById(id);

        if (opPost.isEmpty()) return opPost;

        List<PostHashTag> postTags = getPostTags(opPost.get());

        opPost.get().getExtra().put("postTags", postTags);

        return opPost;
    }

    public List<PostHashTag> getPostTags(Post post) {
        return postHashTagService.getPostTags(post);
    }

    public Optional<Post> findById(Long id) {
        return postRepository.findById(id);
    }

    @Transactional
    public Post write(Member member, String subject, String content, String contentHtml, String hashtagContents) {
        Post post = Post.builder()
                .member(member)
                .subject(subject)
                .content(content)
                .contentHtml(contentHtml)
                .build();

        postRepository.save(post);

        // HashTag와 HashTagContents(Keyword) 저장 후 HashTag 리턴
        // HashTag는 여러 글에서 사용되므로 기존에 존재한다고 RunTimeException 처리 X
        postHashTagService.applyHashTags(member, post, hashtagContents);

        return post;
    }

    public void modify(Member member, Post post, String subject, String content, String contentHtml, String postTagContents) {
        post.setSubject(subject);
        post.setContent(content);
        post.setContentHtml(contentHtml);

        // HashTag와 HashTagContents(Keyword) 저장 후 HashTag 리턴
        postHashTagService.applyHashTags(member, post, postTagContents);
    }

    public void remove(Post post) {
        postRepository.delete(post);
    }

    public boolean actorCanModify(Member member, Post post) {
        return member.getId().equals(post.getMember().getId());
    }

    public boolean actorCanRemove(Member author, Post post) {
        return actorCanModify(author, post);
    }

    public List<PostHashTag> getPostTags(Member author, String postKeywordContent) {
        List<PostHashTag> postTags = postHashTagService.getPostTags(author, postKeywordContent);

        loadForPrintDataOnPostTagList(postTags);

        return postTags;
    }

    private void loadForPrintDataOnPostTagList(List<PostHashTag> postTags) {
        List<Post> posts = postTags
                .stream()
                .map(PostHashTag::getPost)
                .collect(toList());

        loadForPrintData(posts);
    }
}