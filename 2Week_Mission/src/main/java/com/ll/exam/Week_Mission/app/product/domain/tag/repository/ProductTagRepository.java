package com.ll.exam.Week_Mission.app.product.domain.tag.repository;

import com.ll.exam.Week_Mission.app.product.domain.tag.entity.ProductTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductTagRepository extends JpaRepository<ProductTag, Long> {
    Optional<ProductTag> findByProductIdAndProductKeywordId(Long productId, Long keywordId);

    List<ProductTag> findAllByProductId(long productId);

    List<ProductTag> findAllByProductIdIn(long[] ids);

    List<ProductTag> findAllByProductKeyword_contentOrderByProduct_idDesc(String productKeywordContent);
}
