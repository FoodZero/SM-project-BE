package com.sm.project.service.community;

import com.sm.project.domain.community.Post;
import com.sm.project.repository.community.PostRepository;
import com.sm.project.web.dto.community.PostRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final PostQueryService postQueryService;

    public void createPost(Post post) {
        postRepository.save(post);
    }

    public void updatePost(Long postId, PostRequestDTO.UpdateDTO request) {
        Post post = postQueryService.findPostById(postId);
        post.changePost(request.getContent(), request.getLatitude(), request.getLongitude()); //변경감지
    }

    public void deletePost(Long postId) {
        Post post = postQueryService.findPostById(postId);
        postRepository.delete(post);
    }
}
