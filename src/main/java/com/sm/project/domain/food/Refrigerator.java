package com.sm.project.domain.food;

import com.sm.project.domain.Common.BaseDateTimeEntity;
import com.sm.project.domain.member.Member;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "refrigerator", cascade = CascadeType.ALL)
    private List<Food> refrigeratorFoodList = new ArrayList<>();



}
