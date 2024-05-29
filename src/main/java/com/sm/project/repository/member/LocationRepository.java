package com.sm.project.repository.member;

import com.sm.project.domain.member.Location;
import com.sm.project.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findAllByMember(Member member);
}