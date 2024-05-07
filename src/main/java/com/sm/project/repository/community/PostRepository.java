package com.sm.project.repository.community;

import com.sm.project.domain.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
}
