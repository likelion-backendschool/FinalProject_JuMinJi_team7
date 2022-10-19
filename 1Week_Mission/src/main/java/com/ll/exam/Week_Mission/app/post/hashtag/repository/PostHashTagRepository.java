package com.ll.exam.Week_Mission.app.post.hashtag.repository;

import com.ll.exam.Week_Mission.app.post.hashtag.entity.PostHashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostHashTagRepository extends JpaRepository<PostHashTag, Long> {
    Optional<PostHashTag> findByPostIdAndKeywordId(Long articleId, Long keywordId);
}