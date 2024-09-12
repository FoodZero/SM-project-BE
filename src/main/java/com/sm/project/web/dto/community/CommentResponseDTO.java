package com.sm.project.web.dto.community;

import com.sm.project.domain.community.Comment;
import lombok.*;
import org.springframework.data.domain.Slice;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDTO {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CommentListDTO {

        boolean isLast; //마지막 페이지인지 여부
        List<CommentDTO> commentDTOList;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CommentDTO {

        Long commentId; //댓글 아이디 - 대댓글 등록할 때 필요
        String writerName; //작성자명
        String createdAt; //작성시간
        String content; //댓글 내용
        Long parentId; //부모 댓글 아이디
        List<CommentDTO> childList; //자식 댓글 목록
        Boolean isPrivate; //비밀 댓글 여부
    }
}
