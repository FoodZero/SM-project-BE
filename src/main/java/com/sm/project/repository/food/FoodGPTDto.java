package com.sm.project.repository.food;

import lombok.*;

import java.util.Date;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FoodGPTDto {

    private String name;
    private Date expire;
    private Integer count;

    public String toString() {
        //return name + " " + expire + " " + count;
        return name;
    }
}
