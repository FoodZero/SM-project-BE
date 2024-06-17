package com.sm.project.elasticsearch;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "recipe")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecipeDocument {

    @Id
    private String id;
    private String name;
    private String info;
    private String description;
    private String ingredient;
    private Long recommendCount;
    private Boolean bookmark;
}
