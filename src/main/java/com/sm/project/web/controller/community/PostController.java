package com.sm.project.web.controller.community;

import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.code.status.SuccessStatus;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.converter.community.PostConverter;
import com.sm.project.domain.community.Post;
import com.sm.project.domain.enums.PostTopicType;
import com.sm.project.domain.member.Location;
import com.sm.project.domain.member.Member;
import com.sm.project.service.community.PostService;
import com.sm.project.service.member.MemberQueryService;
import com.sm.project.web.dto.community.PostRequestDTO;
import com.sm.project.web.dto.community.PostResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * PostController는 커뮤니티 게시글 관련 API 요청을 처리하는 컨트롤러 클래스입니다.
 * 위치 저장, 조회, 게시글 작성, 조회, 수정, 삭제 등의 기능을 제공합니다.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Post", description = "Post 관련 API")
@RequestMapping("/api/post")
public class PostController {

    private final MemberQueryService memberQueryService;
    private final PostService postService;

    /**
     * 위치 저장 API
     * 
     * @param auth 인증 정보
     * @param request 위치 저장 요청 데이터
     * @return 위치 저장 성공 응답
     */
    @PostMapping("/location")
    @Operation(summary = "위치 저장 API", description = "사용자의 위치를 저장하는 API입니다.")
    public ResponseDTO<?> postLocation(Authentication auth,
                                       @RequestBody PostRequestDTO.LocationDTO request) {

        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString()))
                                          .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        postService.createLocation(member, request);

        return ResponseDTO.of(SuccessStatus.LOCATION_POST_SUCCESS, null);
    }

    /**
     * 위치 조회 API
     * 
     * @param auth 인증 정보
     * @return 저장된 위치 목록 조회 응답
     */
    @GetMapping("/location")
    @Operation(summary = "위치 조회 API", description = "사용자의 저장된 위치 조회 API입니다.")
    public ResponseDTO<PostResponseDTO.LocationListDTO> getLocation(Authentication auth) {

        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString()))
                                          .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        List<Location> locationList = postService.getLocationList(member);

        return ResponseDTO.onSuccess(PostConverter.toLocationList(locationList));
    }

    /**
     * 커뮤니티 글 목록 조회 API
     * 
     * @param auth 인증 정보
     * @param lastIndex 마지막 인덱스 (처음 조회 시 0)
     * @param postTopicType 게시글 유형 (나눔, 레시피, 전체 조회 시 null)
     * @param locationId 위치 ID (위치 조회 후 나온 현재 위치 ID, 전체 조회 시 null)
     * @return 게시글 목록 조회 응답
     */
    @GetMapping("/")
    @Operation(summary = "커뮤니티 글 조회 API", description = "커뮤니티에서 글 조회하는 API입니다. postType: 나눔, 레시피, 빈값(전체 조회)")
    @Parameters(value = {
            @Parameter(name = "lastIndex", description = "첫 조회는 0이고 스크롤 내릴 때마다 마지막 인덱스 입력 (맨 처음인 경우 Null)"),
            @Parameter(name = "postType", description = "나눔, 레시피, 선택하지 않으면 전체 조회"),
            @Parameter(name = "locationId", description = "위치 조회 결과에서 나온 현재 위치 ID (Null인 경우 전체 조회)")
    })
    public ResponseDTO<PostResponseDTO.PostListDTO> getPostList(Authentication auth,
                                                                @RequestParam(value = "lastIndex", required = false) Long lastIndex,
                                                                @RequestParam(value = "postType", required = false) PostTopicType postTopicType,
                                                                @RequestParam(value = "locationId", required = false) Long locationId) {

        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString()))
                                          .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        if (lastIndex == null) {
            lastIndex = 0L;
        }
        List<Post> posts = postService.getPostList(lastIndex, postTopicType, locationId);

        return ResponseDTO.onSuccess(PostConverter.toPostList(posts, member));
    }

    /**
     * 커뮤니티 글 상세 조회 API
     * 
     * @param auth 인증 정보
     * @param postId 게시글 ID
     * @return 게시글 상세 조회 응답
     */
    @GetMapping("/{postId}")
    @Operation(summary = "커뮤니티 글 상세 조회 API", description = "커뮤니티에서 글 상세 조회하는 API입니다.")
    public ResponseDTO<?> getPost(Authentication auth,
                                  @PathVariable(name = "postId") Long postId) {

        Post post = postService.getPost(postId);
        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString()))
                                          .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        return ResponseDTO.onSuccess(PostConverter.toPostDTO(post, member));
    }

    /**
     * 커뮤니티 글 작성 API
     * 
     * @param auth 인증 정보
     * @param request 게시글 작성 요청 데이터
     * @param imgList 이미지 파일 목록
     * @return 게시글 작성 성공 응답
     */
    @PostMapping(value = "/create")
    @Operation(summary = "커뮤니티 글 등록 API", description = "커뮤니티에서 게시글을 등록하는 API입니다. topic: 나눔, 레시피 중 선택, address: 위치 조회 결과의 주소 입력")
    public ResponseDTO<?> createPost(Authentication auth, @RequestPart("request") PostRequestDTO.CreateDTO request, @RequestPart(value= "images", required = false) List<MultipartFile> imgList) {
        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString()))
                                          .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        postService.createPost(request, member, imgList);
        return ResponseDTO.of(SuccessStatus.POST_CREATE_SUCCESS, null);
    }


    /**
     * 커뮤니티 글 수정 API
     * 
     * @param postId 게시글 ID
     * @param request 게시글 수정 요청 데이터
     * @return 게시글 수정 성공 응답
     */
    @PatchMapping("/update/{postId}")
    @Operation(summary = "커뮤니티 글 수정 API", description = "커뮤니티에서 게시글을 수정하는 API입니다. status: 진행 중이면 true, 마감이면 false")
    public ResponseDTO<?> updatePost(@PathVariable(name = "postId") Long postId, @RequestBody PostRequestDTO.UpdateDTO request) {
        postService.updatePost(postId, request);
        return ResponseDTO.of(SuccessStatus.POST_UPDATE_SUCCESS, null);
    }

    /**
     * 커뮤니티 글 삭제 API
     * 
     * @param postId 게시글 ID
     * @return 게시글 삭제 성공 응답
     */
    @DeleteMapping("/delete/{postId}")
    @Operation(summary = "커뮤니티 글 삭제 API", description = "커뮤니티에서 게시글을 삭제하는 API입니다.")
    public ResponseDTO<?> deletePost(@PathVariable(name = "postId") Long postId) {
        postService.deletePost(postId);
        return ResponseDTO.of(SuccessStatus.POST_DELETE_SUCCESS, null);
    }
}
