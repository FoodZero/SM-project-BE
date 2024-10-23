package com.sm.project.domain.community;

import com.sm.project.domain.Common.BaseDateTimeEntity;
import com.sm.project.domain.enums.PostStatusType;
import com.sm.project.domain.enums.PostTopicType;
import com.sm.project.domain.member.Location;
import com.sm.project.domain.member.Member;
import lombok.*;
import org.hibernate.annotations.BatchSize;

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

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(columnDefinition = "VARCHAR(20)", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    private PostTopicType topic;

    @Enumerated(EnumType.STRING)
    private PostStatusType status;

    @BatchSize(size = 5)
    @OneToMany(mappedBy = "post", fetch = FetchType.EAGER)
    private List<PostImg> postImgs = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> commentList = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    public void changePost(String content, PostStatusType postStatusType) {
        this.content = content;
        this.status = postStatusType;
    }

}
