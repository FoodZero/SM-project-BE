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

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Post", description = "Post 관련 API")
@RequestMapping("/api/post")
public class PostController {

    private final MemberQueryService memberQueryService;
    private final PostService postService;


    @PostMapping("/location")
    @Operation(summary = "위치 저장 API", description = "사용자의 위치를 저장하는 api입니다.")
    public ResponseDTO<?> postLocation(Authentication auth,
                                       @RequestBody PostRequestDTO.LocationDTO request){

        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        postService.createLocation(member,request);

        return ResponseDTO.of(SuccessStatus.LOCATION_POST_SUCCESS, null);
    }

    @GetMapping("/location")
    @Operation(summary = "위치 조회 API", description = "사용자의 저장된 위치 조회 api입니다.")
    public ResponseDTO<PostResponseDTO.LocationListDTO> getLocation(Authentication auth){

        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        List<Location> locationList = postService.getLocationList(member);

        return ResponseDTO.onSuccess(PostConverter.toLocationList(locationList));
    }

    @GetMapping("/")
    @Operation(summary = "커뮤니티 글 조회 API", description = "커뮤니티에서 글 조회하는 api입니다.postType: 나눔, 레시피, 빈값(전체 조회) ")
    @Parameters(value = {
            @Parameter(name = "lastIndex", description = "lastIndex 첫 조회는 0이고 스크롤 내릴때마다 마지막 index 입력하시면 됩니다."),
            @Parameter(name = "postType", description = "나눔, 레시피, 선택하지 않으면 전체가 조회됩니다.")

    })
    public ResponseDTO<PostResponseDTO.PostListDTO> getPostList(Authentication auth,
                                                                      @RequestParam(value = "lastIndex", required = false) Long lastIndex,
                                                                      @RequestParam(value = "postType", required = false) PostTopicType postTopicType){

        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        List<Post> posts = postService.getPostList(lastIndex,postTopicType);
        System.out.println(posts.get(0).getContent());

        return ResponseDTO.onSuccess(PostConverter.toPostList(posts, member));
    }


    @GetMapping("/{postId}")
    @Operation(summary = "커뮤니티 글 상세 조회 API", description = "커뮤니티에서 글 상세 조회하는 api입니다.")
    public ResponseDTO<?> getPost(Authentication auth,
                                  @PathVariable(name = "postId") Long postId){

        Post post = postService.getPost(postId);
        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        return ResponseDTO.onSuccess(PostConverter.toPostDTO(post,member));
    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")
    @Operation(summary = "커뮤니티 글 등록 API", description = "커뮤니티에서 게시글을 등록하는 api입니다. topic: 나눔, 레시피 중에 선택, address: 위치 조회에서 나온 address 입력 ")
    public ResponseDTO<?> createPost(Authentication auth, @RequestPart("request") PostRequestDTO.CreateDTO request, @RequestPart("images") List<MultipartFile> imgList) {
        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        postService.createPost(request,member, imgList);
        return ResponseDTO.of(SuccessStatus.POST_CREATE_SUCCESS, null);
    }

    @PatchMapping("/update/{postId}")
    @Operation(summary = "커뮤니티 글 수정 API", description = "커뮤니티에서 게시글을 수정하는 api입니다. status: 진행중이면 true, 마감이면 false")
    public ResponseDTO<?> updatePost(@PathVariable(name = "postId") Long postId, @RequestBody PostRequestDTO.UpdateDTO request) {
        postService.updatePost(postId, request);
        return ResponseDTO.of(SuccessStatus.POST_UPDATE_SUCCESS, null);
    }

    @DeleteMapping("/delete/{postId}")
    @Operation(summary = "커뮤니티 글 삭제 API", description = "커뮤니티에서 게시글을 삭제하는 api입니다.")
    public ResponseDTO<?> deletePost(@PathVariable(name = "postId") Long postId) {
        postService.deletePost(postId);
        return ResponseDTO.of(SuccessStatus.POST_DELETE_SUCCESS, null);
    }


}
