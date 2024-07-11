package com.sm.project.repository.community;

import com.sm.project.domain.community.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = "select c from Comment c where c.post.id = :postId and c.parentComment is null")
    Slice<Comment> findCommentListByPostId(@Param("postId") Long postId, Pageable pageable); //해당 포스트 식별자의 댓글 목록 조회
}
