package com.sm.project.repository.member;

import com.sm.project.domain.member.Location;
import com.sm.project.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * LocationRepository는 위치 엔티티의 데이터베이스 작업을 처리하는 JPA 리포지토리 인터페이스입니다.
 */
public interface LocationRepository extends JpaRepository<Location, Long> {

    /**
     * 특정 회원과 관련된 모든 위치를 조회하는 메서드입니다.
     *
     * @param member 회원 객체
     * @return 위치 목록
     */
    List<Location> findAllByMember(Member member);

    /**
     * 특정 주소에 해당하는 위치를 조회하는 메서드입니다.
     *
     * @param address 주소
     * @return 조회된 위치 객체
     */
    @Query("SELECT l FROM Location l WHERE l.address = :address")
    Optional<Location> findByAddress(String address);
}
