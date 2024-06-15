package com.sm.project.service.community;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.CommentHandler;
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
    private final PostQueryService postQueryService;

    public void createComment(Member member, Post post, CommentRequestDTO.CreateCommentDTO request) {
        Comment comment = CommentConverter.toParentComment(member, post, request);
        commentRepository.save(comment);
    }

    public void createChildComment(Member member, Comment parent, CommentRequestDTO.CreateCommentDTO request) {
        Post post = postQueryService.findPostById(parent.getPost().getId());
        Comment childComment = CommentConverter.toChildComment(member, post, parent, request);
        commentRepository.save(childComment);
    }

    public void updateComment(Member member, Comment comment, CommentRequestDTO.UpdateCommentDTO request) {
        if (comment.getMember() == member) {
            comment.setContent(request.getContent());
        } else throw new CommentHandler(ErrorStatus.COMMENT_NOT_OWNED);
    }
}
