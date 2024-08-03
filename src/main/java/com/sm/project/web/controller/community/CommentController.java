package com.sm.project.web.controller.community;


import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.code.ErrorReasonDTO;
import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.code.status.SuccessStatus;
import com.sm.project.apiPayload.exception.handler.CommentHandler;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.converter.community.CommentConverter;
import com.sm.project.domain.community.Comment;
import com.sm.project.domain.member.Member;
import com.sm.project.service.community.CommentQueryService;
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
import org.springframework.data.domain.Slice;
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
    private final CommentQueryService commentQueryService;

    @PostMapping("/{postId}")
    @Operation(summary = "커뮤니티 댓글 등록 API", description = "커뮤니티에서 댓글을 등록하는 api입니다. postId는 댓글을 등록할 포스트의 식별자입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT200", description = "댓글 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4001", description = "해당 회원을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "POST4001", description = "해당 포스트를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
    })
    public ResponseDTO<?> createComment(Authentication auth, @PathVariable(name = "postId") Long postId, @RequestBody CommentRequestDTO.CreateCommentDTO request) {

        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        commentService.createComment(member, postQueryService.findPostById(postId), request);

        return ResponseDTO.of(SuccessStatus.COMMENT_CREATE_SUCCESS, null);
    }

    @PostMapping("/{commentId}/child")
    @Operation(summary = "커뮤니티 대댓글 등록 API", description = "커뮤니티에서 대댓글을 등록하는 api입니다. commentId는 부모 댓글의 식별자입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT200", description = "댓글 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4001", description = "해당 회원을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "POST4001", description = "해당 포스트를 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT4001", description = "해당 댓글을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
    })
    public ResponseDTO<?> createChildComment(Authentication auth, @PathVariable(name = "commentId") Long commentId, @RequestBody CommentRequestDTO.CreateCommentDTO request) {

        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        commentService.createChildComment(member, commentQueryService.findCommentById(commentId), request);

        return ResponseDTO.of(SuccessStatus.COMMENT_CREATE_SUCCESS, null);
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "커뮤니티 댓글 수정 API", description = "수정할 댓글의 식별자를 입력하고, request body에 수정할 내용을 입력하세요. 댓글 작성자가 아닐 시 예외가 발생합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT2001", description = "댓글 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4001", description = "해당 회원을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT4001", description = "해당 댓글을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT4002", description = "자신이 작성한 댓글이 아닙니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
    })
    public ResponseDTO<?> updateComment(Authentication auth, @PathVariable(name = "commentId") Long commentId, @RequestBody CommentRequestDTO.UpdateCommentDTO request) {

        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        commentService.updateComment(member, commentQueryService.findCommentById(commentId), request);

        return ResponseDTO.of(SuccessStatus.COMMENT_UPDATE_SUCCESS, null);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "커뮤니티 자식이 없는 댓글 삭제 API", description = "자식이 없는 댓글 삭제 입니다! 삭제할 댓글의 식별자를 입력하세요. 댓글 작성자가 아닐 시 예외가 발생합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT2002", description = "댓글 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4001", description = "해당 회원을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT4001", description = "해당 댓글을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT4002", description = "자신이 작성한 댓글이 아닙니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT4003", description = "자식이 존재하는 댓글입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class)))
    })
    public ResponseDTO<?> deleteComment(Authentication auth, @PathVariable(name = "commentId") Long commentId) {

        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Comment comment = commentQueryService.findCommentById(commentId);

        if (comment.getChildComments().isEmpty()) {

            commentService.deleteComment(member, comment);

        } else throw new CommentHandler(ErrorStatus.COMMENT_CHILD_EXIST);

        return ResponseDTO.of(SuccessStatus.COMMENT_DELETE_SUCCESS, null);
    }

    @PatchMapping("/parent/{commentId}")
    @Operation(summary = "커뮤니티 자식이 있는 댓글 삭제 API", description = "자식이 있는 댓글 삭제 입니다! 삭제할 댓글의 식별자를 입력하세요. 댓글 작성자가 아닐 시 예외가 발생합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT2002", description = "댓글 삭제 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4001", description = "해당 회원을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT4001", description = "해당 댓글을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT4002", description = "자신이 작성한 댓글이 아닙니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMENT4004", description = "자식이 존재하지 않는 댓글입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class)))
    })
    public ResponseDTO<?> deleteParentComment(Authentication auth, @PathVariable(name = "commentId") Long commentId) {

        Member member = memberQueryService.findMemberById(Long.valueOf(auth.getName().toString())).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));

        Comment comment = commentQueryService.findCommentById(commentId);

        if (comment.getChildComments().isEmpty()) {
            throw new CommentHandler(ErrorStatus.COMMENT_NOT_PARENT);
        }

        commentService.deleteParentComment(member, comment);

        return ResponseDTO.of(SuccessStatus.COMMENT_DELETE_SUCCESS, null);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "커뮤니티 댓글 조회 API", description = "조회할 댓글 목록의 post 식별자를 입력하고, page의 인덱스를 입력하세요. page의 인덱스는 0부터 시작합니다. 응답에서 last는 마지막 페이지인지의 여부입니다.")
    @ApiResponses()
    public ResponseDTO<?> readCommentList(@PathVariable(name = "postId") Long postId, @RequestParam(name = "page") int page) {

        Slice<Comment> commentList = commentQueryService.findCommentListByPostId(postId, page);

        return ResponseDTO.of(SuccessStatus.COMMENT_READ_SUCCESS, CommentConverter.toCommentListDTO(commentList));

    }
}
