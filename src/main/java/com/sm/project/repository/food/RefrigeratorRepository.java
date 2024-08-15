package com.sm.project.repository.food;

import com.sm.project.domain.food.Refrigerator;
import com.sm.project.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * RefrigeratorRepository는 냉장고 엔티티의 데이터베이스 작업을 처리하는 JPA 리포지토리 인터페이스입니다.
 */
public interface RefrigeratorRepository extends JpaRepository<Refrigerator, Long> {

    /**
     * 특정 회원과 관련된 모든 냉장고를 조회하는 메서드입니다.
     *
     * @param member 회원 객체
     * @return 냉장고 목록
     */
    List<Refrigerator> findAllByMember(Member member);

    /**
     * 특정 ID와 회원에 해당하는 냉장고를 조회하는 메서드입니다.
     *
     * @param id 냉장고 ID
     * @param member 회원 객체
     * @return 조회된 냉장고 객체
     */
    Optional<Refrigerator> findByIdAndMember(Long id, Member member);

    void deleteByMemberAndId(Member member, Long id);
}
