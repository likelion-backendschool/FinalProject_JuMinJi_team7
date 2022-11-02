package com.ll.exam.Week_Mission.app.product.domain.keyword.repository;

import com.ll.exam.Week_Mission.app.product.domain.keyword.entity.ProductKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductKeywordRepository extends JpaRepository<ProductKeyword, Long> {
    Optional<ProductKeyword> findByContent(String keywordContent);
}
