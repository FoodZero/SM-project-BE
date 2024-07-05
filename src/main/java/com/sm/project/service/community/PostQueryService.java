package com.sm.project.service.community;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.PostHandler;
import com.sm.project.domain.community.Post;
import com.sm.project.repository.community.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * PostQueryService는 게시글 조회 관련 기능을 제공하는 서비스 클래스입니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostQueryService {

    private final PostRepository postRepository;

    /**
     * ID를 통해 게시글을 조회하는 메서드입니다.
     *
     * @param postId 게시글 ID
     * @return 게시글 객체
     * @throws PostHandler 게시글을 찾을 수 없는 경우 예외 발생
     */
    public Post findPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_FOUND));
    }
}
