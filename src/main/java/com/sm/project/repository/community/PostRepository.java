package com.sm.project.repository.community;

import com.sm.project.domain.community.Post;
import com.sm.project.domain.enums.PostTopicType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    @Query("SELECT p FROM Post p WHERE (:postTopicType IS NULL OR p.topic = :postTopicType) AND p.id > :lastIndex ORDER BY p.id ASC")
    List<Post> findPostList(Long lastIndex, PostTopicType postTopicType);
}
