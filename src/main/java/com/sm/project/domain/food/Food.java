package com.sm.project.domain.food;

import com.sm.project.domain.Common.BaseDateTimeEntity;
import com.sm.project.domain.enums.FoodType;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Food extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "food_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String name;

    @Temporal(TemporalType.DATE)
    private Date expire;

    @Column(nullable = false)
    @ColumnDefault("0")
    private Integer count;

    @Enumerated(EnumType.STRING)
    private FoodType foodType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refrigerator_id")
    private Refrigerator refrigerator;


}
