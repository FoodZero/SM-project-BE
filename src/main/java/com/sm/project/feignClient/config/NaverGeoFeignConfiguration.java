package com.sm.project.feignClient.config;

import com.sm.project.feignClient.FeignClientException;
import feign.Logger;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class NaverGeoFeignConfiguration {

    @Value("${naver.geo.client}")
    private String client;

    @Value("${naver.geo.key}")
    private String key;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            requestTemplate.header("X-NCP-APIGW-API-KEY-ID", client);
            requestTemplate.header("X-NCP-APIGW-API-KEY", key);
        };
    }
    @Bean
    public ErrorDecoder errorDecoder() {
        return new FeignClientException();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }

}
