package com.ll.exam.Week_Mission.app.post.hashtag.service;

import com.ll.exam.Week_Mission.app.member.entity.Member;
import com.ll.exam.Week_Mission.app.post.entity.Post;
import com.ll.exam.Week_Mission.app.post.hashtag.entity.PostHashTag;
import com.ll.exam.Week_Mission.app.post.hashtag.repository.PostHashTagRepository;
import com.ll.exam.Week_Mission.app.post.keyword.entity.PostKeyword;
import com.ll.exam.Week_Mission.app.post.keyword.service.PostKeywordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostHashTagService {

    private final PostKeywordService postKeywordService;
    private final PostHashTagRepository postHashTagRepository;

    public void applyHashTags(Member member, Post post, String keywordContentsStr) {
        List<String> keywordContents = Arrays.stream(keywordContentsStr.split("#"))
                .map(String::trim)
                .filter(s -> s.length() > 0)
                .collect(Collectors.toList());

        keywordContents.forEach(keywordContent -> {
            saveHashTag(member, post, keywordContent);
        });
    }


    @Transactional
    public PostHashTag saveHashTag(Member member, Post post, String postKeywordContent) {
        // PostHashKeyword가 생성되면 PostKeyword도 생성되므로, PostKeyword도 디비 영속화 처리
        PostKeyword postkeyword = postKeywordService.save(postKeywordContent);

        // 중복 검사
        Optional<PostHashTag> opHashTag = postHashTagRepository.findByPostIdAndPostkeywordId(post.getId(), postkeyword.getId());
        if (opHashTag.isPresent()) {
            return opHashTag.get();
        }

        // 중복이 아니면 저장
        PostHashTag posthashtag = PostHashTag.builder()
                .member(member)
                .post(post)
                .postkeyword(postkeyword)
                .build();

        postHashTagRepository.save(posthashtag);

        return posthashtag;
    }
    public List<PostHashTag> getPostTags(Post post) {
        return postHashTagRepository.findAllByPostId(post.getId());
    }

    public List<PostHashTag> findByPostId(long PostId){
        return postHashTagRepository.findByPostId(PostId);
    }

    public List<PostHashTag> getPostTagsByPostIdIn(long[] ids) {
        return postHashTagRepository.findAllByPostIdIn(ids);
    }
}