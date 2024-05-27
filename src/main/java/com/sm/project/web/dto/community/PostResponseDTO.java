package com.sm.project.web.dto.community;

import lombok.*;

import java.util.List;

public class PostResponseDTO {

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LocationDTO {
        Long id;
        String address;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class LocationListDTO {
        List<LocationDTO> LocationList;
    }
}
