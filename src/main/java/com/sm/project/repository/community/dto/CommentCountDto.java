package com.sm.project.repository.community.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
//게시글 목록 조회 시 댓글 수 조회를 위한 dto
public class CommentCountDto {
    private Long postId;
    private Long commentCount;
}
