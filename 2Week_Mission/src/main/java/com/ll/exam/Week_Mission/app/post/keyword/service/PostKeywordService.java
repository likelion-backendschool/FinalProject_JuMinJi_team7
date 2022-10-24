package com.ll.exam.Week_Mission.app.post.keyword.service;

import com.ll.exam.Week_Mission.app.post.keyword.entity.PostKeyword;
import com.ll.exam.Week_Mission.app.post.keyword.repository.PostKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Transactional
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

    public Optional<PostKeyword> findById(long id) {
        return postKeywordRepository.findById(id);
    }

    public PostKeyword findByContentOrSave(String content) {
        return save(content);
    }
}
