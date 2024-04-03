package com.sm.project.feignClient.lambda;

import com.sm.project.feignClient.config.LambdaFeignConfiguration;
import com.sm.project.feignClient.dto.LambdaRequest;
import com.sm.project.feignClient.dto.LambdaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Component
@FeignClient(name = "LambdaFeignClient", url = "${cloud.aws.lambda.url}", configuration = LambdaFeignConfiguration.class)
public interface LambdaFeignClient {

    @PostMapping("/default")
    LambdaResponse getFood(@RequestBody LambdaRequest foodRequest);
}
