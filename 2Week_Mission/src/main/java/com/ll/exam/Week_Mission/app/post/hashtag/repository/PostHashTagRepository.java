package com.ll.exam.Week_Mission.app.post.hashtag.repository;

import com.ll.exam.Week_Mission.app.post.entity.Post;
import com.ll.exam.Week_Mission.app.post.hashtag.entity.PostHashTag;
import com.ll.exam.Week_Mission.app.product.domain.tag.entity.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostHashTagRepository extends JpaRepository<PostHashTag, Long> {

    List<PostHashTag> findByPostId(Long PostId);

    Optional<PostHashTag> findByPostIdAndPostkeywordId(Long PostId, Long keywordId);
    List<PostHashTag> findAllByPostIdIn(long[] ids);
    List<PostHashTag> findAllByPostId(Long postId);

    List<PostHashTag> findAllByMemberIdAndPostkeyword_contentOrderByPost_idDesc(Long id, String postKeywordContent);

    List<PostHashTag> findAllByMemberIdAndPostkeywordIdOrderByPost_idDesc(long memberId, long postKeywordId);

    List<ProductTag> findAllByPostkeyword_contentOrderByPost_idDesc(String productKeywordContent);

    List<PostHashTag> findByMemberId(Long memberId);

    List<PostHashTag> findAllByMemberIdAndPostkeywordId(long authorId, long postKeywordId);
}