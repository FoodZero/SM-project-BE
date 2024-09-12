package com.sm.project.web.dto.community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class CommentRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCommentDTO {

        private String content;
        private Boolean isPrivate;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCommentDTO {

        private String content;
        private Boolean isPrivate;
    }
}
