package com.sm.project.feignClient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NaverOCRRequest {

    private String version;
    private String requestId;
    private long timestamp;
    private String lang;
    private List<Image> images;
    private Boolean enableTableDetection;
}
