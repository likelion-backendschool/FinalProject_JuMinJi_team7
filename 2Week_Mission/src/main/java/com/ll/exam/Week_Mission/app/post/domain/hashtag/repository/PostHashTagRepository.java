package com.ll.exam.Week_Mission.app.post.domain.hashtag.repository;

import com.ll.exam.Week_Mission.app.post.domain.hashtag.entity.PostHashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostHashTagRepository extends JpaRepository<PostHashTag, Long> {
    Optional<PostHashTag> findByPostIdAndPostkeywordId(Long PostId, Long keywordId);
    List<PostHashTag> findAllByPostIdIn(long[] ids);
    List<PostHashTag> findAllByPostId(Long postId);
    List<PostHashTag> findAllByMemberIdAndPostkeywordIdOrderByPost_idDesc(long memberId, long postKeywordId);
    List<PostHashTag> findByMemberId(Long memberId);
    List<PostHashTag> findAllByMemberIdAndPostkeywordId(long authorId, long postKeywordId);
    List<PostHashTag> findAllByMemberIdAndPostkeyword_contentOrderByPost_idDesc(Long id, String postKeywordContent);
}