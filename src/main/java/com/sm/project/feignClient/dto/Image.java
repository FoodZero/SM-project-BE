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
public class Image {
    private String format;
    private String name;
    private String url;
    private List<Long> templateIds;
}
