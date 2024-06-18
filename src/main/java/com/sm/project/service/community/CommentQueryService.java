package com.sm.project.service.community;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.CommentHandler;
import com.sm.project.domain.community.Comment;
import com.sm.project.repository.community.CommentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommentQueryService {

    private final CommentRepository commentRepository;

    public Comment findCommentById(Long id) {
        return commentRepository.findById(id).orElseThrow(() -> new CommentHandler(ErrorStatus.COMMENT_NOT_FOUND));
    }

    public Slice<Comment> findCommentListByPostId(Long postId) {

        PageRequest pageRequest = PageRequest.of(0, 2); //페이지 0부터 시작해서 2개씩 조회(더보기 방식)
        return commentRepository.findCommentListByPostId(postId, pageRequest);
    }
}
