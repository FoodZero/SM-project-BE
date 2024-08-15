package com.sm.project.service.recipe;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.apiPayload.exception.handler.RecipeHandler;
import com.sm.project.apiPayload.exception.handler.RecommendHandler;
import com.sm.project.domain.food.Recipe;
import com.sm.project.domain.food.Recommend;
import com.sm.project.domain.member.Member;
import com.sm.project.repository.food.RecipeRepository;
import com.sm.project.repository.food.RecommendRepository;
import com.sm.project.repository.member.MemberRepository;
import com.sm.project.service.member.MemberQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RecommendService {

    private final RecommendRepository recommendRepository;
    private final MemberQueryService memberQueryService;
    private final RecipeRepository recipeRepository;

    /**
     * 레시피에 추천을 눌렀을 때 저장하는 메소드입니다.
     * @param memberId
     * @param recipeId
     */
    @Transactional
    public void saveRecommend(Long memberId, Long recipeId) {
        Member member = memberQueryService.findMemberById(memberId).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeHandler(ErrorStatus.RECIPE_NOT_FOUND));

        Recommend recommend = Recommend.builder()
                .member(member)
                .recipe(recipe)
                .build();

        if (!recommendRepository.existsByMemberIdAndRecipeId(memberId, recipeId)) {
            recommendRepository.save(recommend);
            recipe.addRecommendCount();
        } else throw new RecipeHandler(ErrorStatus.RECOMMEND_EXIST);
    }

    /**
     * 레시피의 추천을 해제하는 메소드입니다.
     * @param memberId
     * @param recipeId
     */
    @Transactional
    public void deleteRecommend(Long memberId, Long recipeId) {
        Recommend recommend = recommendRepository.findByMemberIdAndRecipeId(memberId, recipeId);
        if (recommend != null) {
            recommendRepository.delete(recommend);
            Recipe recipe = recipeRepository.findById(recipeId).orElseThrow(() -> new RecipeHandler(ErrorStatus.RECIPE_NOT_FOUND));
            recipe.subRecommendCount();
        } else throw new RecommendHandler(ErrorStatus.RECOMMEND_NOT_FOUND);
    }
}
