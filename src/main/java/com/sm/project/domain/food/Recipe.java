package com.sm.project.domain.food;


import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String name; // 이름

    @Column(columnDefinition = "TEXT")
    private String info; // 몇인분, 조리시간

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;//레시피

    @Column(nullable = false, columnDefinition = "TEXT")
    private String ingredient;// 재료

    @Column(nullable = false)
    @ColumnDefault("0")
    private Long recommendCount;// 추천수

    @Column(columnDefinition = "BOOLEAN")
    @ColumnDefault("false")
    private Boolean bookmark;


}
