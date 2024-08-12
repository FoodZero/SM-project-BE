package com.sm.project.domain.food;


import com.sm.project.domain.member.Member;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@DynamicInsert
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_gpt_id")
    private Member member; //해당 멤버가 gpt로 생성한 레시피임을 나타냄
}
