package com.sm.project.repository.member;

import com.sm.project.domain.member.Member;
import com.sm.project.domain.member.MemberPassword;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * MemberPasswordRepository는 회원 비밀번호 엔티티의 데이터베이스 작업을 처리하는 JPA 리포지토리 인터페이스입니다.
 */
public interface MemberPasswordRepository extends JpaRepository<MemberPassword, Long> {

    /**
     * 특정 회원에 해당하는 비밀번호를 조회하는 메서드입니다.
     *
     * @param member 회원 객체
     * @return 조회된 회원 비밀번호 객체
     */
    MemberPassword findByMember(Member member);
}
