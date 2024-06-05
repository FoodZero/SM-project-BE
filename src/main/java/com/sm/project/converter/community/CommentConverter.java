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
}
