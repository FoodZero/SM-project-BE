package com.sm.project.web.controller.community;


import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.code.ErrorReasonDTO;
import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.code.status.SuccessStatus;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.domain.community.Post;
import com.sm.project.domain.member.Member;
import com.sm.project.service.community.CommentService;
import com.sm.project.service.community.PostQueryService;
import com.sm.project.service.member.MemberQueryService;
import com.sm.project.web.dto.community.CommentRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Comment", description = "Comment 관련 API")
@RequestMapping("/api/comment")
public class CommentController {

    private final MemberQueryService memberQueryService;
    private final PostQueryService postQueryService;
    private final CommentService commentService;

    @PostMapping("/{postId}/create")
    @Operation(summary = "커뮤니티 댓글 등록 API", description = "커뮤니티에서 댓글을 등록하는 api입니다.")
    @ApiResponses({
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT200", description = "댓글 등록 성공"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4001", description = "해당 회원을 찾을 수 없습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "POST4001", description = "해당 포스트를 찾을 수 없습니다.",
                            content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
    })
    public ResponseDTO<?> createComment(Authentication auth, @PathVariable(name = "postId") Long postId, @RequestBody CommentRequestDTO.CreateCommentDTO request) {
        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
        Post post = postQueryService.findPostById(postId);
        commentService.createComment(member, post, request);
        return ResponseDTO.of(SuccessStatus.COMMENT_CREATE_SUCCESS, null);
    }
}
