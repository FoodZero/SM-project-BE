package com.sm.project.web.dto.community;

import com.sm.project.domain.enums.PostStatusType;
import lombok.*;

import java.time.LocalDateTime;
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

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PostDTO{
        Long id;
        String nickname;
        String address;
        String title;
        String content;
        PostStatusType status;
        LocalDateTime createdAt;
        List<PostImgResponseDTO> itemImgUrlList;
        Integer commentCount;

    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PostDetailDTO{
        Long id;
        String nickname;
        String address;
        String title;
        String content;
        PostStatusType status;
        LocalDateTime createdAt;
        List<PostImgResponseDTO> itemImgUrlList;


    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostImgResponseDTO {
        String itemImgUrl;
    }

    @Builder
    @Getter
    @AllArgsConstructor(access = AccessLevel.PROTECTED)
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class PostListDTO{
        List<PostDTO> postDTOList;
    }
}
