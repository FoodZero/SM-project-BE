package com.sm.project.converter.community;

import com.sm.project.domain.community.Comment;
import com.sm.project.domain.community.Post;
import com.sm.project.domain.member.Member;
import com.sm.project.web.dto.community.CommentRequestDTO;
import com.sm.project.web.dto.community.CommentResponseDTO;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.stream.Collectors;

public class CommentConverter {

    public static Comment toParentComment(Member member, Post post, CommentRequestDTO.CreateCommentDTO request) { //원댓글
         Comment comment = Comment.builder()
                        .content(request.getContent())
                        .member(member)
                        .parentComment(null)
                        .build();
         comment.setPost(post);
         return comment;
    }

    public static Comment toChildComment(Member member, Post post, Comment parent, CommentRequestDTO.CreateCommentDTO request) {
        Comment comment = Comment.builder()
                .content(request.getContent())
                .member(member)
                .build();

        comment.setPost(post);
        comment.createChildComments(parent);

        return comment;
    }

    public static CommentResponseDTO.CommentListDTO toCommentListDTO(Slice<Comment> commentList) {

        List<CommentResponseDTO.CommentDTO> commentListDto =
                commentList.stream().map(c -> CommentResponseDTO.CommentDTO.builder()
                        .commentId(c.getId())
                        .writerName(c.getMember().getNickname())
                        .time(c.getCreatedAt())
                        .content(c.getContent())
                        .build())
                .collect(Collectors.toList());

        return CommentResponseDTO.CommentListDTO.builder().commentDTOList(commentListDto).build();
    }
}
