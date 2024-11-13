package com.sm.project.service.community;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.CommentHandler;
import com.sm.project.converter.community.CommentConverter;
import com.sm.project.domain.community.Comment;
import com.sm.project.domain.community.Post;
import com.sm.project.domain.member.Member;
import com.sm.project.firebase.FcmService;
import com.sm.project.repository.community.CommentRepository;
import com.sm.project.web.dto.community.CommentRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostQueryService postQueryService;
    private final FcmService fcmService;

    public void createComment(Member member, Post post, CommentRequestDTO.CreateCommentDTO request) {

        commentRepository.save(CommentConverter.toParentComment(member, post, request));
    }

    public void createChildComment(Member member, Comment parent, CommentRequestDTO.CreateCommentDTO request) throws IOException{
        Post post = postQueryService.findPostById(parent.getPost().getId());

        commentRepository.save(CommentConverter.toChildComment(member, post, parent, request));
    }

    public void updateComment(Member member, Comment comment, CommentRequestDTO.UpdateCommentDTO request) {
        if(!comment.getMember().equals(member)) throw new CommentHandler(ErrorStatus.COMMENT_NOT_OWNED); //본인이 작성한 댓글이 아니면 수정 불가
        comment.changeComment(request.getContent(), request.getIsPrivate());
    }

    public void deleteComment(Member member, Comment comment) {
        if (!comment.getMember().equals(member)) throw new CommentHandler(ErrorStatus.COMMENT_NOT_OWNED); //본인이 작성한 댓글이 아니면 삭제 불가

        Comment parent = comment.getParentComment();
        if (parent != null && parent.getChildComments().size() == 1 && parent.getIsDeleted()) {
            commentRepository.delete(parent); //자식 혼자 남고, 이미 삭제된 부모면 부모도 삭제
        }
        commentRepository.delete(comment);
    }

    public void deleteParentComment(Member member, Comment comment) {
        if (!comment.getMember().equals(member)) throw new CommentHandler(ErrorStatus.COMMENT_NOT_OWNED); //본인이 작성한 댓글이 아니면 삭제 불가

        comment.deleteParentComment();
        comment.changeComment("삭제된 댓글입니다.", comment.getIsPrivate());
    }
}
