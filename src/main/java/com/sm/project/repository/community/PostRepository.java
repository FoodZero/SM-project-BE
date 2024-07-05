package com.sm.project.repository.community;

import com.sm.project.domain.community.Post;
import com.sm.project.domain.enums.PostTopicType;
import com.sm.project.domain.member.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * PostRepository는 게시글 엔티티의 데이터베이스 작업을 처리하는 JPA 리포지토리 인터페이스입니다.
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    /**
     * 게시글 목록을 조회하는 쿼리 메서드입니다.
     * 
     * @param lastIndex 마지막 인덱스
     * @param postTopicType 게시글 주제 타입 (null 허용)
     * @param location 위치 객체 (null 허용)
     * @return 게시글 목록
     */
    @Query("SELECT p FROM Post p WHERE (:postTopicType IS NULL OR p.topic = :postTopicType) AND p.id > :lastIndex AND (:location IS NULL OR p.location = :location) ORDER BY p.id ASC")
    List<Post> findPostList(Long lastIndex, PostTopicType postTopicType, Location location);
}
