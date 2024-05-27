package com.sm.project.feignClient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NaverGeoRequest {

    String request;
    String coords;
    String output;
    String orders;
}
