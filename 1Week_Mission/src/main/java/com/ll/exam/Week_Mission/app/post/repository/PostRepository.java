package com.ll.exam.Week_Mission.app.post.repository;

import com.ll.exam.Week_Mission.app.post.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
