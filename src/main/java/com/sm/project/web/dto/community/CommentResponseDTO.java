package com.sm.project.web.dto.community;

import com.sm.project.domain.community.Comment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDTO {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CommentListDTO {

        List<CommentDTO> commentDTOList;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class CommentDTO {

        Long commentId; //댓글 아이디 - 대댓글 등록할 때 필요
        String writerName; //작성자명
        LocalDateTime time; //작성시간
        String content; //댓글 내용
    }
}
