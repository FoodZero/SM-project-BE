package com.sm.project.web.dto.community;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PostRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateDTO {
        private double latitude; //사용자의 위도, 경도
        private double longitude;
        private String content; //글 내용
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateDTO {
        private double latitude; //사용자의 위도, 경도
        private double longitude;
        private String content; //글 내용
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LocationDTO {
        private double latitude; //사용자의 위도, 경도
        private double longitude;
    }
}
