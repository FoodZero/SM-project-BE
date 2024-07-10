package com.sm.project.repository.food;

import com.sm.project.domain.enums.FoodType;
import com.sm.project.domain.food.Food;
import com.sm.project.domain.food.Refrigerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * FoodRepository는 음식 엔티티의 데이터베이스 작업을 처리하는 JPA 리포지토리 인터페이스입니다.
 */
public interface FoodRepository extends JpaRepository<Food, Long> {

    /**
     * 특정 냉장고에서 유통기한이 가장 임박한 상위 5개의 음식을 조회하는 메서드입니다.
     *
     * @param refrigerator 냉장고 객체
     * @return 유통기한이 임박한 음식 목록
     */
    List<Food> findTop5ByRefrigeratorOrderByExpireDesc(Refrigerator refrigerator);

    /**
     * 특정 냉장고에 있는 모든 음식을 조회하는 메서드입니다.
     *
     * @param refrigerator 냉장고 객체
     * @return 음식 목록
     */
    List<Food> findAllByRefrigerator(Refrigerator refrigerator);

    /**
     * 특정 냉장고와 음식 ID에 해당하는 음식을 조회하는 메서드입니다.
     *
     * @param refrigerator 냉장고 객체
     * @param foodId 음식 ID
     * @return 조회된 음식 객체
     */
    Optional<Food> findByRefrigeratorAndId(Refrigerator refrigerator, Long foodId);

    /**
     * 특정 냉장고에 있는 음식을 업데이트하는 메서드입니다.
     *
     * @param name 음식 이름
     * @param count 음식 개수
     * @param expire 유통기한
     * @param foodType 음식 종류
     * @param foodId 음식 ID
     * @param refrigerator 냉장고 객체
     */
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Food a SET a.name = :name, a.count = :count, a.expire = :expire, a.foodType = :foodType WHERE a.refrigerator = :refrigerator and a.id = :foodId")
    void changeFood(String name, Integer count, Date expire, FoodType foodType, Long foodId, Refrigerator refrigerator);

    /**
     * 멤버 아이디를 통해 냉장고와 음식을 조인해서 음식의 이름, 유통기한, 개수를 조회하는 메소드입니다.
     * @param memberId
     * @return FoodDto 리스트
     */
    @Query("select new com.sm.project.repository.food.FoodGPTDto(f.name, f.expire, f.count) from Food f " +
            "join Refrigerator r on f.refrigerator = r " +
            "join Member m on r.member = m " +
            "where m.id = :memberId " +
            "order by f.expire asc"
            )
    List<FoodGPTDto> findFoodGptDto(@Param("memberId") Long memberId);
}
