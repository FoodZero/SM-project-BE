package com.sm.project.domain.image;

import com.sm.project.domain.Common.BaseDateTimeEntity;
import lombok.*;

import javax.persistence.*;
@Entity
@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Uuid extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uuid;
}
