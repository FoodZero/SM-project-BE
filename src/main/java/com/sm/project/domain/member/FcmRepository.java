package com.sm.project.domain.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FcmRepository extends JpaRepository<FcmToken, Long> {

    Optional<FcmToken> findByTokenAndSerialNumber(String token, String serialNumber);

    List<FcmToken> findAllByMember(Member member);
}
