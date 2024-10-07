package com.sm.project.repository.member;

import com.sm.project.domain.food.Refrigerator;
import com.sm.project.domain.mapping.MemberRefrigerator;
import com.sm.project.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MemberRefrigeratorRepository extends JpaRepository<MemberRefrigerator, Long> {
    /**
     * 특정 회원과 관련된 모든 냉장고를 조회하는 메서드입니다.
     *
     * @param member 회원 객체
     * @return 냉장고 목록
     */
    List<MemberRefrigerator> findByMember(Member member);

    /**
     * 냉장고에 등록된 사용자들을 조회하는 메서드입니다.
     * @param refrigeratorId
     * @return
     */
    @Query("select mr.member from MemberRefrigerator mr where mr.refrigerator.id = :refrigeratorId")
    List<Member> findMembersByRefrigeratorId(@Param("refrigeratorId") Long refrigeratorId);

    /**
     * 냉장고에 등록된 사용자를 삭제한느 메서드입니다.
     * @param memberId
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM MemberRefrigerator mr WHERE mr.member.id = :memberId and mr.refrigerator.id = :refrigeratorId")
    void deleteByMemberId(Long memberId, Long refrigeratorId);

    /**
     * 냉장고에 등록된 모든 사용자를 삭제한느 메서드입니다.
     * @param refrigeratorId
     */
    @Modifying
    @Transactional
    @Query("DELETE FROM MemberRefrigerator mr WHERE mr.refrigerator.id = :refrigeratorId and mr.member.id != :memberId")
    void deleteByRefrigeratorId(Long refrigeratorId, Long memberId);


    List<MemberRefrigerator> findByRefrigerator(Refrigerator refrigerator);

}
