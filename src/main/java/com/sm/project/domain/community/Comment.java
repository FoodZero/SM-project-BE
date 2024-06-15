package com.sm.project.domain.community;

import com.sm.project.domain.Common.BaseDateTimeEntity;
import com.sm.project.domain.member.Member;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@DynamicInsert
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    /**
     * 대댓글 관련
     * parentComment: 부모 댓글을 참조하는 필드
     * childComments: 현재 댓글에 대한 대댓글들의 목록을 나타내는 필드
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_comment_id")
    //@ColumnDefault()
    private Comment parentComment;

    @OneToMany(mappedBy = "parentComment")
    private List<Comment> childComments = new ArrayList<>();

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    public void createChildComments(Comment parentComment) { //대댓글 생성할 때 사용. 부모, 자식 관계 설정
        this.parentComment = parentComment;
        parentComment.childComments.add(this);
    }

    public void setPost(Post post) { //양방향 연관관계 편의 메서드
        this.post = post;
        post.getCommentList().add(this);
    }

    public void setContent(String content) {
        this.content = content;
    }
}
