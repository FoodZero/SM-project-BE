package com.sm.project.service;

import com.sm.project.repository.community.CommentRepository;
import com.sm.project.repository.community.dto.CommentCountDto;
import feign.Param;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentCacheService {
    private static final String KEY_PREFIX = "post:commentCount:";
    private final CommentRepository commentRepository;
    private final StringRedisTemplate redisTemplate;

    //댓글 수 조회 - 캐싱
    //@Cacheable(value = "commentCount", key = "'posts:' + #postIds")
    /*public Map<Long, Long> getCommentCountMap(List<Long> postIds) {
        List<CommentCountDto> commentCount = commentRepository.countCommentByPostId(postIds);
        return commentCount.stream().collect(Collectors.toMap(
                CommentCountDto::getPostId,
                CommentCountDto::getCommentCount
        ));
    }*/

    @Cacheable(value = "commentCount", key = "'commentCount:postId:' + #postId")
    public Long getCommentCount(Long postId) {
        return commentRepository.countByPost(postId);
    }
}
