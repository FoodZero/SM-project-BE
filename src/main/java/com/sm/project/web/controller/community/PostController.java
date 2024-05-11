package com.sm.project.web.controller.community;

import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.code.status.SuccessStatus;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.converter.community.PostConverter;
import com.sm.project.domain.community.Post;
import com.sm.project.domain.member.Member;
import com.sm.project.service.community.PostQueryService;
import com.sm.project.service.community.PostService;
import com.sm.project.service.member.MemberQueryService;
import com.sm.project.web.dto.community.PostRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Post", description = "Post 관련 API")
@RequestMapping("/api/post")
public class PostController {

    private final MemberQueryService memberQueryService;
    private final PostService postService;

    @PostMapping("/create")
    @Operation(summary = "커뮤니티 글 등록 API", description = "커뮤니티에서 게시글을 등록하는 api입니다.")
    public ResponseDTO<?> createPost(Authentication auth, @RequestBody PostRequestDTO.CreateDTO request) {
        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        postService.createPost(PostConverter.toPost(member, request));
        return ResponseDTO.of(SuccessStatus.POST_CREATE_SUCCESS, null);
    }

    @PatchMapping("/update/{postId}")
    @Operation(summary = "커뮤니티 글 수정 API", description = "커뮤니티에서 게시글을 수정하는 api입니다.")
    public ResponseDTO<?> updatePost(@PathVariable Long postId, @RequestBody PostRequestDTO.UpdateDTO request) {
        postService.updatePost(postId, request);
        return ResponseDTO.of(SuccessStatus.POST_UPDATE_SUCCESS, null);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "커뮤니티 글 삭제 API", description = "커뮤니티에서 게시글을 삭제하는 api입니다.")
    public ResponseDTO<?> deletePost() {
        return null;
    }
}
