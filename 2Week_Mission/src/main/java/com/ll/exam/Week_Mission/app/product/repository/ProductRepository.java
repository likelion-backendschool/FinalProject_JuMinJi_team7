package com.ll.exam.Week_Mission.app.product.repository;

import com.ll.exam.Week_Mission.app.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByOrderByIdDesc();
}