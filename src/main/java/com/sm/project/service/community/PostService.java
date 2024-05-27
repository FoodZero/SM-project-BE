package com.sm.project.service.community;

import com.sm.project.domain.community.Post;
import com.sm.project.domain.community.PostImg;
import com.sm.project.repository.community.PostImgRepository;
import com.sm.project.repository.community.PostRepository;
import com.sm.project.service.UtilService;
import com.sm.project.web.dto.community.PostRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final PostQueryService postQueryService;
    private final UtilService utilService;
    private final PostImgRepository postImgRepository;

    public void createPost(Post post, List<MultipartFile> imgList) {
        postRepository.save(post);

        //이미지 업로드
        for (MultipartFile multipartFile : imgList) {
            String imgUrl = utilService.uploadS3Img("post", multipartFile);
            PostImg newPostImg = PostImg.builder()
                    .url(imgUrl)
                    .name(multipartFile.getOriginalFilename())
                    .post(post)
                    .build();

            postImgRepository.save(newPostImg);
        }
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
