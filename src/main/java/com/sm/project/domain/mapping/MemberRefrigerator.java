package com.sm.project.domain.mapping;

import com.sm.project.domain.Common.BaseDateTimeEntity;
import com.sm.project.domain.food.Refrigerator;
import com.sm.project.domain.member.Member;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "member_refrigerator")
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRefrigerator extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_refrigerator_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "refrigerator_id")
    private Refrigerator refrigerator;
}
