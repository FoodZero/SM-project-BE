package com.sm.project.feignClient.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
public class NaverOCRResponse {
    private String version;
    private String requestId;
    private long timestamp;
    private List<Image> images;

    @Data
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Image {
        private String uid;
        private String name;
        private String inferResult;
        private String message;
        private MatchedTemplate matchedTemplate;
        private ValidationResult validationResult;
        private Title title;
        private List<Field> fields;

        // getters and setters
    }
    @Data
    @Getter
    @Setter
    @NoArgsConstructor
    static class MatchedTemplate {
        private int id;
        private String name;

        // getters and setters
    }
    @Data
    @Getter
    @Setter
    @NoArgsConstructor
    static class ValidationResult {
        private String result;

        // getters and setters
    }
    @Data
    @Getter
    @Setter
    @NoArgsConstructor
    static class Title {
        private String name;
        private String inferText;
        private double inferConfidence;

        // getters and setters
    }
    @Data
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Field {
        private String inferText;

        // getters and setters
    }



}
