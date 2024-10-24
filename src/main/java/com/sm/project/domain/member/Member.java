package com.sm.project.domain.member;


import com.sm.project.domain.Common.BaseDateTimeEntity;
import com.sm.project.domain.community.Post;
import com.sm.project.domain.enums.JoinType;
import com.sm.project.domain.enums.StatusType;
import com.sm.project.domain.image.ReceiptImage;
import com.sm.project.domain.mapping.MemberRefrigerator;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(nullable = false, columnDefinition = "VARCHAR(30)")
    private String email;

    @Column(nullable = false, columnDefinition = "VARCHAR(20)")
    private String nickname;

    @Column(nullable = false, columnDefinition = "VARCHAR(13)")
    private String phone;

    @Enumerated(EnumType.STRING)
    private JoinType joinType;

    @Column(columnDefinition = "BOOLEAN")
    @ColumnDefault("false")
    private Boolean infoAgree;

    @Column(columnDefinition = "BOOLEAN")
    @ColumnDefault("false")
    private Boolean messageAgree;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusType status = StatusType.ACTIVE;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<MemberRefrigerator> memberRefrigeratorList = new ArrayList<>();

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private MemberPassword memberPassword;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<ReceiptImage> memberReceiptImageList = new ArrayList<>();


    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<FcmToken> fcmTokenList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Post> postList;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Location> locationList;

    public Member deleteMember(){
        this.status = StatusType.INACTIVE;
        return this;
    }
}
