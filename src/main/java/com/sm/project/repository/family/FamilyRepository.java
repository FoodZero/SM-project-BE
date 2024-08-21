package com.sm.project.repository.family;

import com.sm.project.domain.family.Family;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FamilyRepository extends JpaRepository<Family, Long> {
}
