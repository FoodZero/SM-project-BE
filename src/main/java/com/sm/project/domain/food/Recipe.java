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

    @Column(columnDefinition = "boolean default false")
    private Boolean isDeleted; //gpt레시피만 쓰는 필드(초기화 여부 표시)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="member_gpt_id")
    private Member member; //해당 멤버가 gpt로 생성한 레시피임을 나타냄. null이면 일반 레시피

    public void addRecommendCount() {
        this.recommendCount++;
    }

    public void subRecommendCount() {
        this.recommendCount--;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }
}
