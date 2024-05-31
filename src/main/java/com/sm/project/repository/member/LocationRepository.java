package com.sm.project.repository.member;

import com.sm.project.domain.member.Location;
import com.sm.project.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findAllByMember(Member member);


    @Query("SELECT l FROM Location l WHERE l.address = :address")
    Optional<Location> findByAddress(String address);
}
