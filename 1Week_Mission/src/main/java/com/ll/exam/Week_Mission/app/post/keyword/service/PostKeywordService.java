package com.ll.exam.Week_Mission.app.post.keyword.service;

import com.ll.exam.Week_Mission.app.post.keyword.entity.PostKeyword;
import com.ll.exam.Week_Mission.app.post.keyword.repository.PostKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostKeywordService {
    private final PostKeywordRepository postKeywordRepository;

    public PostKeyword save(String keywordContent) {
        Optional<PostKeyword> optKeyword = postKeywordRepository.findByContent(keywordContent);

        if (optKeyword.isPresent()) {
            return optKeyword.get();
        }

        PostKeyword postkeyword = PostKeyword.builder()
                .content(keywordContent)
                .build();

        postKeywordRepository.save(postkeyword);

        return postkeyword;
    }
}
