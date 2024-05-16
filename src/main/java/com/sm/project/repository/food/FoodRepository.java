package com.sm.project.repository.food;

import com.sm.project.domain.enums.FoodType;
import com.sm.project.domain.food.Food;
import com.sm.project.domain.food.Refrigerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface FoodRepository extends JpaRepository<Food, Long> {


    List<Food> findTop5ByRefrigeratorOrderByExpireDesc(Refrigerator refrigerator);


    List<Food> findAllByRefrigerator(Refrigerator refrigerator);

    Optional<Food> findByRefrigeratorAndId(Refrigerator refrigerator, Long foodId);

    @Transactional
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Food a SET  a.name = :name, a.count = :count, a.expire = :expire, a.foodType = :foodType WHERE a.refrigerator =:refrigerator and a.id = :foodId")
    void changeFood(String name, Integer count, Date expire, FoodType foodType, Long foodId, Refrigerator refrigerator);
}
