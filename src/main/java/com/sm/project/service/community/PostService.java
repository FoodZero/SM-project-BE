package com.sm.project.service.community;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.PostHandler;
import com.sm.project.converter.community.PostConverter;
import com.sm.project.domain.community.Post;
import com.sm.project.domain.community.PostImg;
import com.sm.project.domain.enums.PostStatusType;
import com.sm.project.domain.enums.PostTopicType;
import com.sm.project.domain.member.Location;
import com.sm.project.domain.member.Member;
import com.sm.project.feignClient.dto.NaverGeoResponse;
import com.sm.project.feignClient.naver.NaverGeoFeignClient;
import com.sm.project.repository.community.PostImgRepository;
import com.sm.project.repository.community.PostRepository;
import com.sm.project.repository.member.LocationRepository;
import com.sm.project.service.UtilService;
import com.sm.project.web.dto.community.PostRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * PostService는 게시글 관련 기능을 제공하는 서비스 클래스입니다.
 * 게시글 생성, 수정, 삭제 및 위치 관련 기능을 담당합니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class PostService {

    private final PostRepository postRepository;
    private final PostQueryService postQueryService;
    private final LocationRepository locationRepository;
    private final NaverGeoFeignClient naverGeoFeignClient;
    private final UtilService utilService;
    private final PostImgRepository postImgRepository;

    /**
     * 새로운 게시글을 생성하는 메서드입니다.
     *
     * @param request 게시글 생성 요청 데이터
     * @param member 회원 객체
     * @param imgList 이미지 파일 목록
     */
    public void createPost(PostRequestDTO.CreateDTO request, Member member, List<MultipartFile> imgList) {

        Location location = locationRepository.findByAddress(request.getAddress(), member)
            .orElseThrow(() -> new PostHandler(ErrorStatus.LOCATION_NOT_FOUND));

        Post post = PostConverter.toPost(member, request, location);

        postRepository.save(PostConverter.toPost(member, request, location));

        // 이미지 업로드
        for (MultipartFile multipartFile : imgList) {

            String imgUrl = utilService.uploadS3Img("post", multipartFile);

            PostImg newPostImg = PostConverter.toPostImg(imgUrl,multipartFile);

            newPostImg.changePost(post);

            postImgRepository.save(newPostImg);

        }
    }

    public void createPost2(PostRequestDTO.CreateDTO request, Member member) {
        Location location = locationRepository.findByAddress(request.getAddress(), member)
                .orElseThrow(() -> new PostHandler(ErrorStatus.LOCATION_NOT_FOUND));
        Post post = PostConverter.toPost(member, request, location);
        postRepository.save(post);
    }

    /**
     * 게시글을 수정하는 메서드입니다.
     *
     * @param postId 게시글 ID
     * @param request 게시글 수정 요청 데이터
     */
    public void updatePost(Long postId, PostRequestDTO.UpdateDTO request) {

        Post post = postQueryService.findPostById(postId);

        if (request.isStatus()) {
            post.changePost(request.getContent(), PostStatusType.PROCEEDING); // 변경 감지

        } else {
            post.changePost(request.getContent(), PostStatusType.END);
        }
    }

    /**
     * 게시글을 삭제하는 메서드입니다.
     *
     * @param postId 게시글 ID
     */
    public void deletePost(Long postId) {

        postRepository.delete(postQueryService.findPostById(postId));
    }

    /**
     * 회원의 위치를 생성하는 메서드입니다.
     *
     * @param member 회원 객체
     * @param request 위치 생성 요청 데이터
     */
    public void createLocation(Member member, PostRequestDTO.LocationDTO request) {
        String coords = request.getLongitude() + "," + request.getLatitude();
        NaverGeoResponse naverGeoResponse = naverGeoFeignClient.generateLocation("coordsToaddr", coords, "epsg:4326", "json", "legalcode");
        Optional<Location> location = locationRepository.findByAddress(naverGeoResponse.getResults().get(0).getRegion().getArea3().getName(), member);

        if (!location.isPresent()) {
            Location newLocation = PostConverter.toLocation(request, naverGeoResponse,member);
            locationRepository.save(newLocation);
        }
    }

    /**
     * 회원의 모든 위치 목록을 조회하는 메서드입니다.
     *
     * @param member 회원 객체
     * @return 위치 목록
     */
    public List<Location> getLocationList(Member member) {
        return locationRepository.findAllByMember(member);
    }

    /**
     * 게시글 목록을 조회하는 메서드입니다.
     *
     * @param lastIndex 마지막 인덱스
     * @param postTopicType 게시글 주제 타입
     * @param locationId 위치 ID
     * @return 게시글 목록
     */
    public List<Post> getPostList(Long lastIndex, PostTopicType postTopicType, Long locationId) {

        Location location = (locationId == null) ? null : locationRepository.findById(locationId)
            .orElseThrow(() -> new PostHandler(ErrorStatus.LOCATION_NOT_FOUND));

        return postRepository.findPostList(lastIndex, postTopicType, location);
    }

    /**
     * 특정 게시글을 조회하는 메서드입니다.
     *
     * @param postId 게시글 ID
     * @return 게시글 객체
     */
    public Post getPost(Long postId) {

        return postRepository.findById(postId)
            .orElseThrow(() -> new PostHandler(ErrorStatus.POST_NOT_FOUND));
    }
}
