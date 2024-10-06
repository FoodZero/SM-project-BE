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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMember = mock(Member.class);
        mockCreateRequest = mock(PostRequestDTO.CreateDTO.class);
        mockUpdateRequest = mock(PostRequestDTO.UpdateDTO.class);
        mockLocationRequest = mock(PostRequestDTO.LocationDTO.class);
        mockLocation = mock(Location.class);
        mockPost = mock(Post.class);
        mockMultipartFile = mock(MultipartFile.class);
    }

    @Test
    void createPost() {
        // Given
        NaverGeoResponse naverGeoResponse = mock(NaverGeoResponse.class);
        NaverGeoResponse.Result result = mock(NaverGeoResponse.Result.class);
        NaverGeoResponse.Region region = mock(NaverGeoResponse.Region.class);
        NaverGeoResponse.Area area3 = mock(NaverGeoResponse.Area.class);

        when(naverGeoFeignClient.generateLocation(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(naverGeoResponse);
        when(naverGeoResponse.getResults()).thenReturn(List.of(result));
        when(result.getRegion()).thenReturn(region);
        when(region.getArea3()).thenReturn(area3);
        when(area3.getName()).thenReturn("Seoul");
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
        when(locationRepository.findByAddress(anyString(), eq(mockMember)))
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
        NaverGeoResponse naverGeoResponse = mock(NaverGeoResponse.class);
        NaverGeoResponse.Result result = mock(NaverGeoResponse.Result.class);
        NaverGeoResponse.Region region = mock(NaverGeoResponse.Region.class);

        when(result.getRegion()).thenReturn(region);
        when(region.getArea3()).thenReturn(mock(NaverGeoResponse.Area.class));
        when(naverGeoResponse.getResults()).thenReturn(List.of(result));

        when(naverGeoFeignClient.generateLocation(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(naverGeoResponse);
        when(locationRepository.findByAddress(anyString(), eq(mockMember)))
                .thenReturn(Optional.empty());

        // When
        postService.createLocation(mockMember, mockLocationRequest);

        // Then
        verify(locationRepository, times(1)).save(any(Location.class));
    }

    @Test
    void createLocationAlreadyExists() {
        // Given
        NaverGeoResponse naverGeoResponse = mock(NaverGeoResponse.class);
        NaverGeoResponse.Result result = mock(NaverGeoResponse.Result.class);
        NaverGeoResponse.Region region = mock(NaverGeoResponse.Region.class);
        NaverGeoResponse.Area area3 = mock(NaverGeoResponse.Area.class);

        when(naverGeoFeignClient.generateLocation(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(naverGeoResponse);

        when(naverGeoResponse.getResults()).thenReturn(List.of(result));
        when(result.getRegion()).thenReturn(region);
        when(region.getArea3()).thenReturn(area3);
        when(area3.getName()).thenReturn("Seoul");

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
        when(postRepository.findPostList(anyLong(), any(PostTopicType.class), any(Location.class)))
                .thenReturn(List.of(mockPost));

        // When
        List<Post> posts = postService.getPostList(1L, PostTopicType.SHARE, 1L);

        // Then
        assertNotNull(posts);
        assertEquals(1, posts.size());
        verify(postRepository, times(1)).findPostList(anyLong(), any(PostTopicType.class), any(Location.class));
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
