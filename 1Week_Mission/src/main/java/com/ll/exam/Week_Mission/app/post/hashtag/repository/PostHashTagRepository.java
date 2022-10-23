package com.ll.exam.Week_Mission.app.post.hashtag.repository;

import com.ll.exam.Week_Mission.app.post.entity.Post;
import com.ll.exam.Week_Mission.app.post.hashtag.entity.PostHashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostHashTagRepository extends JpaRepository<PostHashTag, Long> {

    List<PostHashTag> findByPostId(Long PostId);

    Optional<PostHashTag> findByPostIdAndPostkeywordId(Long PostId, Long keywordId);
    List<PostHashTag> findAllByPostIdIn(long[] ids);

}