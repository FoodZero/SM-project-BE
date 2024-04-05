package com.sm.project.feignClient.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class LambdaRequest {
    private List<String> food;
}
