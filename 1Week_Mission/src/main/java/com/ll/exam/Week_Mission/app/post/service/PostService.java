package com.ll.exam.Week_Mission.app.post.service;

import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.post.entity.Post;
import com.ll.exam.Week_Mission.app.post.hashtag.entity.PostHashTag;
import com.ll.exam.Week_Mission.app.post.hashtag.service.PostHashTagService;
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
            List<PostHashTag> postHashTags = postTagsByPostIdsMap.get(post.getId());

            if (postHashTags == null || postHashTags.size() == 0) return;

            post.getExtra().put("PostHashTag", postHashTags);
        });
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
}