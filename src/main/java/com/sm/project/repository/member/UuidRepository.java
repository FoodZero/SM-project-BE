package com.sm.project.repository.member;

import com.sm.project.domain.image.Uuid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UuidRepository extends JpaRepository<Uuid, Long> {
}
