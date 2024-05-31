package com.sm.project.web.dto.community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PostRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDTO {
        private String title;
        private String content;
        private String topic;
        private String address;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDTO {

        private String content; //글 내용
        private boolean status;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDTO {
        private double latitude; //사용자의 위도, 경도
        private double longitude;
    }
}
