package com.sm.project.repository.food;

import com.sm.project.domain.food.Recipe;
import com.sm.project.domain.member.Member;
import com.sm.project.repository.chatgpt.RecipeNameDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    boolean existsByNameAndIdNot(String name, Long recipeId);

    @Query("select new com.sm.project.repository.chatgpt.RecipeNameDto(r.name) from Recipe r " +
            "join Member m on r.member = m " +
            "where m.id = :memberId and r.isDeleted = false")
    List<RecipeNameDto> findRecipeName(@Param("memberId") Long memberId);

    List<Recipe> findByMemberId(Long memberId);

    @Query("select r from Recipe r where r.member = :member and r.isDeleted = false")
    List<Recipe> findByMemberIdAndIsDeleted(@Param("member")Member member);

    @Query("select r from Recipe r " +
            "join Bookmark b on b.recipe = r " +
            "where b.member = :member")
    Slice<Recipe> findByMemberIdAndBookmark(@Param("member")Member member, Pageable pageable);
}
