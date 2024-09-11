package com.sm.project.repository.food;

import com.sm.project.domain.food.Refrigerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * RefrigeratorRepository는 냉장고 엔티티의 데이터베이스 작업을 처리하는 JPA 리포지토리 인터페이스입니다.
 */
public interface RefrigeratorRepository extends JpaRepository<Refrigerator, Long> {
    @Modifying
    @Query("UPDATE Refrigerator r SET r.name = :newName WHERE r.id = :id")
    void updateRefrigeratorName(@Param("id") Long id, @Param("newName") String newName);
}
