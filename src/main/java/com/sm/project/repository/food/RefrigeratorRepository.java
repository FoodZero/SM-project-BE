package com.sm.project.repository.food;

import com.sm.project.domain.food.Refrigerator;
import com.sm.project.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefrigeratorRepository extends JpaRepository<Refrigerator, Long> {

    List<Refrigerator> findAllByMember(Member member);

    Optional<Refrigerator> findByIdAndMember(Long id, Member member);
}
