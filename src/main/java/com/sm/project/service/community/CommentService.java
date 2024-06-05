package com.sm.project.service.community;

import com.sm.project.converter.community.CommentConverter;
import com.sm.project.domain.community.Comment;
import com.sm.project.domain.community.Post;
import com.sm.project.domain.member.Member;
import com.sm.project.repository.community.CommentRepository;
import com.sm.project.web.dto.community.CommentRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;

    public void createComment(Member member, Post post, CommentRequestDTO.CreateCommentDTO request) {
        Comment comment = CommentConverter.toParentComment(member, post, request);
        commentRepository.save(comment);
    }
}
