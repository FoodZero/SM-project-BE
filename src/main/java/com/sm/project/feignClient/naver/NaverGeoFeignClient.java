package com.sm.project.feignClient.naver;


import com.sm.project.feignClient.config.NaverGeoFeignConfiguration;
import com.sm.project.feignClient.dto.NaverGeoResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Component
@FeignClient(name = "NaverGeoFeignClient", url = "${naver.geo.url}", configuration = NaverGeoFeignConfiguration.class)
public interface NaverGeoFeignClient {

    @GetMapping(value = "/map-reversegeocode/v2/gc")
    NaverGeoResponse generateLocation(@RequestParam("request") String request,
                                      @RequestParam("coords") String coords,
                                      @RequestParam("sourcecrs") String sourcecrs,
                                      @RequestParam("output") String output,
                                      @RequestParam("orders") String orders);

}
