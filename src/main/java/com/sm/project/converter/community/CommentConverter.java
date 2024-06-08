package com.sm.project.converter.community;

import com.sm.project.domain.community.Comment;
import com.sm.project.domain.community.Post;
import com.sm.project.domain.member.Member;
import com.sm.project.web.dto.community.CommentRequestDTO;

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
}
