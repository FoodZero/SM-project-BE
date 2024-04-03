package com.sm.project.feignClient.naver;

import com.sm.project.feignClient.config.NaverOCRFeignConfiguration;
import com.sm.project.feignClient.dto.NaverOCRRequest;
import com.sm.project.feignClient.dto.NaverOCRResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(name = "NaverOCRFeignClient", url = "${naver.ocr.url}", configuration = NaverOCRFeignConfiguration.class)
public interface NaverOCRFeignClient {

    @PostMapping(value = "/general")
    NaverOCRResponse generateText(@RequestBody NaverOCRRequest request);

}
