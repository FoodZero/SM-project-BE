package com.sm.project.service.community;

import com.sm.project.converter.community.CommentConverter;
import com.sm.project.domain.community.Comment;
import com.sm.project.domain.community.Post;
import com.sm.project.domain.member.Member;
import com.sm.project.firebase.FcmService;
import com.sm.project.repository.community.CommentRepository;
import com.sm.project.web.dto.community.CommentRequestDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostQueryService postQueryService;

    @Mock
    private FcmService fcmService;

    @InjectMocks
    private CommentService commentService;

    private Member member;
    private Post post;
    private Comment parentComment;
    private Comment childComment;
    private Comment comment;
    private CommentRequestDTO.CreateCommentDTO createRequest;
    private CommentRequestDTO.UpdateCommentDTO updateRequest;

    @BeforeEach
    void setUp() {
        // builder 패턴을 사용하여 실제 객체 생성
        member = Member.builder()
                .id(1L)
                .build();

        post = Post.builder()
                .id(1L)
                .title("Test Post")
                .content("This is a test post")
                .commentList(new ArrayList<>())
                .build();

        parentComment = Comment.builder()
                .id(1L)
                .post(post)
                .childComments(new ArrayList<>())
                .content("This is a parent comment")
                .build();

        comment = Comment.builder()
                .id(2L)
                .post(post)
                .content("This is a comment")
                .build();

        childComment = Comment.builder()
                .id(3L)
                .post(post)
                .content("This is a child comment")
                .parentComment(parentComment)
                .build();

        createRequest = new CommentRequestDTO.CreateCommentDTO();
        updateRequest = new CommentRequestDTO.UpdateCommentDTO();

    }
    @Test
    void 댓글생성테스트() throws Exception {
        // Given
        Comment comment = CommentConverter.toParentComment(member, post, createRequest);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        // When
        commentService.createComment(member, post, createRequest);

        // Then
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void 자식_댓글_생성_테스트() throws Exception {
        // Given
        when(postQueryService.findPostById(anyLong())).thenReturn(post);
        Comment childComment = CommentConverter.toChildComment(member, post, parentComment, createRequest);
        when(commentRepository.save(any(Comment.class))).thenReturn(childComment);

        // When
        commentService.createChildComment(member, parentComment, createRequest);

        // Then
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void 댓글_수정_테스트() {
        // Given
        comment = mock(Comment.class); // 모킹된 comment 객체
        when(comment.getMember()).thenReturn(member);



        // When
        commentService.updateComment(member, comment, updateRequest);

        // Then
        verify(comment, times(1)).changeComment(updateRequest.getContent(), updateRequest.getIsPrivate());
    }

    @Test
    void 댓글_삭제_테스트() {
        // Given
        comment = mock(Comment.class); // 모킹된 comment 객체
        when(comment.getMember()).thenReturn(member);
        when(comment.getParentComment()).thenReturn(parentComment);

        // childComments 리스트가 null이 되지 않도록 빈 리스트 반환
        when(comment.getChildComments()).thenReturn(new ArrayList<>());

        // When
        commentService.deleteComment(member, comment);

        // Then
        verify(commentRepository, times(1)).delete(comment);
    }
}
