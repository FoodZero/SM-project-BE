package com.sm.project.repository.food;

import com.sm.project.domain.enums.FoodType;
import com.sm.project.domain.food.Food;
import com.sm.project.domain.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long> {


    List<Food> findAllByMemberAndRefrigeratorId(Member member, Integer refrigeratorId);

    List<Food> findTop5ByMemberOrderByExpireDesc(Member member);

    Food findByMemberAndIdAndRefrigeratorId(Member member, Long foodId, Integer refrigeratorId);
    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Food a SET  a.name = :name, a.count = :count, a.expire = :expire, a.foodType = :foodType WHERE a.member = :member and a.refrigeratorId = :refrigeratorId and a.id = :foodId")
    void changeFood(String name, Integer count, Date expire, FoodType foodType, Member member, Integer refrigeratorId, Long foodId);
}
