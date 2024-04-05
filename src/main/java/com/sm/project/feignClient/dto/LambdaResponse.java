package com.sm.project.feignClient.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Data
@Getter
@Setter
@NoArgsConstructor
public class LambdaResponse {

    public int statusCode;
    public Map<String, String> headers;
    public String body;

}
