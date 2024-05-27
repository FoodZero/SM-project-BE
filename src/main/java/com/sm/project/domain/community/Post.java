package com.sm.project.domain.community;

import com.sm.project.domain.Common.BaseDateTimeEntity;
import com.sm.project.domain.member.Location;
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
public class Post extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String title;
    private String topic; //enum으로 고치기(나눔, 레시피, 잡담)
    private String address;
    private String status; //enum(진행 중, 마감)
    private String imageUrl; //url 여러 이미지면 어쩌지

    @OneToMany(mappedBy = "post")
    private List<PostImg> postImgs = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    public void changePost(String content, double latitude, double longitude) {
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
