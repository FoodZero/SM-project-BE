package com.sm.project.domain.food;

import com.sm.project.domain.Common.BaseDateTimeEntity;
import com.sm.project.domain.mapping.MemberRefrigerator;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Refrigerator extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refrigerator_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String name;

    @OneToMany(mappedBy = "refrigerator", cascade = CascadeType.ALL)
    private List<MemberRefrigerator> memberRefrigeratorList = new ArrayList<>();

    @OneToMany(mappedBy = "refrigerator", cascade = CascadeType.ALL)
    private List<Food> refrigeratorFoodList = new ArrayList<>();


}
