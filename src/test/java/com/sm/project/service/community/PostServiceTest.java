package com.sm.project.service.community;

import com.sm.project.apiPayload.exception.handler.PostHandler;
import com.sm.project.domain.community.Post;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostQueryService postQueryService;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private NaverGeoFeignClient naverGeoFeignClient;

    @Mock
    private UtilService utilService;

    @Mock
    private PostImgRepository postImgRepository;

    @InjectMocks
    private PostService postService;

    private Member mockMember;
    private PostRequestDTO.CreateDTO mockCreateRequest;
    private PostRequestDTO.UpdateDTO mockUpdateRequest;
    private PostRequestDTO.LocationDTO mockLocationRequest;
    private Location mockLocation;
    private Post mockPost;
    private MultipartFile mockMultipartFile;

    private NaverGeoResponse naverGeoResponse;
    private NaverGeoResponse.Result result;
    private NaverGeoResponse.Region region;
    private NaverGeoResponse.Area area;

    @BeforeEach
    void setUp() {
        mockMember = mock(Member.class);
        mockCreateRequest = mock(PostRequestDTO.CreateDTO.class);
        mockUpdateRequest = mock(PostRequestDTO.UpdateDTO.class);
        mockLocationRequest = mock(PostRequestDTO.LocationDTO.class);
        mockLocation = mock(Location.class);
        mockPost = mock(Post.class);
        mockMultipartFile = mock(MultipartFile.class);

        naverGeoResponse = mock(NaverGeoResponse.class);
        result = mock(NaverGeoResponse.Result.class);
        region = mock(NaverGeoResponse.Region.class);
        area = mock(NaverGeoResponse.Area.class);

    }

    @Test
    void createPost() {
        // Given
        when(naverGeoFeignClient.generateLocation(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(naverGeoResponse);
        when(naverGeoResponse.getResults()).thenReturn(List.of(result));
        when(result.getRegion()).thenReturn(region);
        when(region.getArea3()).thenReturn(area);
        when(area.getName()).thenReturn("Seoul");
        when(locationRepository.findByAddress(anyString(), eq(mockMember)))
                .thenReturn(Optional.of(mockLocation));

        // When
        postService.createLocation(mockMember, mockLocationRequest);

        // Then
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void createPostThrowException() {
        // Given
        when(locationRepository.findByAddress(null, mockMember))
                .thenReturn(Optional.empty());

        // When, Then
        assertThrows(PostHandler.class, () ->
                postService.createPost(mockCreateRequest, mockMember, List.of(mockMultipartFile)));
    }



    @Test
    void updatePost() {
        // Given
        when(postQueryService.findPostById(anyLong())).thenReturn(mockPost);
        when(mockUpdateRequest.isStatus()).thenReturn(true);
        when(mockUpdateRequest.getContent()).thenReturn("New Content"); // 컨텐츠 추가

        // When
        postService.updatePost(1L, mockUpdateRequest);

        // Then
        verify(mockPost, times(1)).changePost("New Content", PostStatusType.PROCEEDING);
    }

    @Test
    void deletePost() {
        // Given
        when(postQueryService.findPostById(anyLong())).thenReturn(mockPost);

        // When
        postService.deletePost(1L);

        // Then
        verify(postRepository, times(1)).delete(mockPost);
    }

    @Test
    void createLocation() {
        // Given
        when(result.getRegion()).thenReturn(region);
        when(region.getArea3()).thenReturn(mock(NaverGeoResponse.Area.class));
        when(naverGeoResponse.getResults()).thenReturn(List.of(result));

        when(naverGeoFeignClient.generateLocation(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(naverGeoResponse);
        when(locationRepository.findByAddress(null, mockMember))
                .thenReturn(Optional.empty());

        // When
        postService.createLocation(mockMember, mockLocationRequest);

        // Then
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void createLocationAlreadyExists() {
        // Given
        when(naverGeoFeignClient.generateLocation(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(naverGeoResponse);

        when(naverGeoResponse.getResults()).thenReturn(List.of(result));
        when(result.getRegion()).thenReturn(region);
        when(region.getArea3()).thenReturn(area);
        when(area.getName()).thenReturn("Seoul");

        //Location이 이미 존재하는 경우
        when(locationRepository.findByAddress(anyString(), eq(mockMember)))
                .thenReturn(Optional.of(mockLocation));

        // When
        postService.createLocation(mockMember, mockLocationRequest);

        // Then
        verify(locationRepository, never()).save(any(Location.class));
    }

    @Test
    void getLocationList() {
        // Given
        when(locationRepository.findAllByMember(mockMember)).thenReturn(List.of(mockLocation));

        // When
        List<Location> locations = postService.getLocationList(mockMember);

        // Then
        assertNotNull(locations);
        assertEquals(1, locations.size());
        verify(locationRepository, times(1)).findAllByMember(mockMember);
    }

    @Test
    void getPostList() {
        // Given
        when(locationRepository.findById(anyLong())).thenReturn(Optional.of(mockLocation));
        when(postRepository.findPostList(anyLong(), any(PostTopicType.class), any(Location.class), any(Pageable.class)))
                .thenReturn(List.of(mockPost));

        // When
        List<Post> posts = postService.getPostList(1L, PostTopicType.SHARE, 1L);

        // Then
        assertNotNull(posts);
        assertEquals(1, posts.size());
        verify(postRepository, times(1)).findPostList(anyLong(), any(PostTopicType.class), any(Location.class), any(Pageable.class));
    }

    @Test
    void getPost() {
        // Given
        when(postRepository.findById(anyLong())).thenReturn(Optional.of(mockPost));

        // When
        Post post = postService.getPost(1L);

        // Then
        assertNotNull(post);
        verify(postRepository, times(1)).findById(anyLong());
    }

    @Test
    void getPostThrowException() {
        // Given
        when(postRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When / Then
        assertThrows(PostHandler.class, () -> postService.getPost(1L));
    }
}
