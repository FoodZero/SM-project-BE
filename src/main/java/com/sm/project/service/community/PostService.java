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


    public void createPost(PostRequestDTO.CreateDTO request, Member member, List<MultipartFile> imgList) {

        Location location = locationRepository.findByAddress(request.getAddress()).orElseThrow(() -> new PostHandler(ErrorStatus.LOCATION_NOT_FOUND));
        Post post = PostConverter.toPost(member,request,location);
        postRepository.save(post);

        //이미지 업로드
        for (MultipartFile multipartFile : imgList) {
            String imgUrl = utilService.uploadS3Img("post", multipartFile);
            PostImg newPostImg = PostImg.builder()
                    .url(imgUrl)
                    .name(multipartFile.getOriginalFilename())
                    .build();

            newPostImg.changePost(post);
            postImgRepository.save(newPostImg);
        }
    }

    public void updatePost(Long postId, PostRequestDTO.UpdateDTO request) {
        Post post = postQueryService.findPostById(postId);
        if(request.isStatus()){
            post.changePost(request.getContent(), PostStatusType.PROCEEDING);//변경 감지
        }else{
            post.changePost(request.getContent(), PostStatusType.END);
        }

    }

    public void deletePost(Long postId) {
        Post post = postQueryService.findPostById(postId);
        postRepository.delete(post);
    }

    public void createLocation(Member member, PostRequestDTO.LocationDTO request){

        String coords = request.getLongitude() + "," + request.getLatitude();
        NaverGeoResponse naverGeoResponse = naverGeoFeignClient.generateLocation("coordsToaddr",coords,"epsg:4326","json","legalcode");

        Optional<Location> location = locationRepository.findByAddress(naverGeoResponse.getResults().get(0).getRegion().getArea3().getName());

        if(!location.isPresent()) {

            Location newLocation = Location.builder()
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .address(naverGeoResponse.getResults().get(0).getRegion().getArea3().getName())
                    .member(member)
                    .build();

            locationRepository.save(newLocation);
        }


    }

    public List<Location> getLocationList(Member member){

        return locationRepository.findAllByMember(member);

    }

    public List<Post> getPostList(Long lastIndex, PostTopicType postTopicType){

        return postRepository.findPostList(lastIndex, postTopicType);
    }

}
