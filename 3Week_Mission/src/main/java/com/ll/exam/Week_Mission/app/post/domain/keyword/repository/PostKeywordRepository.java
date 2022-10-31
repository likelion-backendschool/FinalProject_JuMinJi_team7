package com.ll.exam.Week_Mission.app.post.domain.keyword.repository;

import com.ll.exam.Week_Mission.app.post.domain.keyword.entity.PostKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostKeywordRepository extends JpaRepository<PostKeyword, Long> {
    PostKeyword findByContent(String keywordContent);
}
