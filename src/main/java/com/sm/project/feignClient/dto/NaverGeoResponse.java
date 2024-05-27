package com.sm.project.feignClient.dto;

import lombok.Data;
import java.util.List;

@Data
public class NaverGeoResponse {

    private Status status;
    private List<Result> results;

    @Data
    public static class Status {
        private int code;
        private String name;
        private String message;
    }

    @Data
    public static class Result {
        private String name;
        private Code code;
        private Region region;
    }

    @Data
    public static class Code {
        private String id;
        private String type;
        private String mappingId;
    }

    @Data
    public static class Region {
        private Area area0;
        private Area area1;
        private Area area2;
        private Area area3;
        private Area area4;
    }

    @Data
    public static class Area {
        private String name;
        private Coords coords;
    }

    @Data
    public static class Coords {
        private String crs;
        private double x;
        private double y;
    }
}
