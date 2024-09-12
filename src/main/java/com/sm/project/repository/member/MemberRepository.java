package com.sm.project.repository.member;

import com.sm.project.domain.food.Refrigerator;
import com.sm.project.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * MemberRepository는 회원 엔티티의 데이터베이스 작업을 처리하는 JPA 리포지토리 인터페이스입니다.
 */
public interface MemberRepository extends JpaRepository<Member, Long> {

    /**
     * 이메일을 통해 회원을 조회하는 메서드입니다.
     *
     * @param email 회원 이메일
     * @return 조회된 회원 객체
     */
    Optional<Member> findByEmail(String email);

    /**
     * 전화번호를 통해 회원을 조회하는 메서드입니다.
     *
     * @param phone 회원 전화번호
     * @return 조회된 회원 객체
     */
    Optional<Member> findByPhone(String phone);



    /**
     * 닉네임을 통해 회원을 조회하는 메서드입니다.
     *
     * @param nickname 회원 닉네임
     * @return 조회된 회원 객체
     */
    Member findByNickname(String nickname);

    /**
     * 특정 냉장고를 소유한 회원을 조회하는 메서드입니다.
     *
     * @param refrigerator 냉장고 객체
     * @return 조회된 회원 객체
     */
    Member findByMemberRefrigeratorListContaining(Refrigerator refrigerator);


    /**
     * 닉네임 변경 메서드
     */
    @Modifying
    @Query("UPDATE Member m SET m.nickname = :newName WHERE m.id = :id")
    int updateMemberName(@Param("id") Long id, @Param("newName") String newName);
}

