package com.ll.exam.Week_Mission.app.post.domain.keyword.service;

import com.ll.exam.Week_Mission.app.post.domain.keyword.entity.PostKeyword;
import com.ll.exam.Week_Mission.app.post.domain.keyword.repository.PostKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class PostKeywordService {
    private final PostKeywordRepository postKeywordRepository;

    public PostKeyword save(String keywordContent) {
        PostKeyword optKeyword = postKeywordRepository.findByContent(keywordContent);

        if (optKeyword!=null) {
            return optKeyword;
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

    // BaseInitData 생성 테스트용 메서드
    public long findIdByContent(String keywordContent) {
        return postKeywordRepository.findByContent(keywordContent).getId();
    }
}
