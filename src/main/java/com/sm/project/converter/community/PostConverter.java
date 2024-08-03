package com.sm.project.converter.community;

import com.sm.project.domain.community.Post;
import com.sm.project.domain.community.PostImg;
import com.sm.project.domain.enums.PostStatusType;
import com.sm.project.domain.enums.PostTopicType;
import com.sm.project.domain.member.Location;
import com.sm.project.domain.member.Member;
import com.sm.project.feignClient.dto.NaverGeoResponse;
import com.sm.project.web.dto.community.PostRequestDTO;
import com.sm.project.web.dto.community.PostResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



public class PostConverter {

    public static Post toPost(Member member, PostRequestDTO.CreateDTO request, Location location) {
        PostTopicType topic;


        switch (request.getTopic()) {
            case "나눔":
                topic = PostTopicType.SHARE;
                break;

            case "레시피":
                topic = PostTopicType.RECIPE;
                break;

            default:
                topic = PostTopicType.ETC;
        }

        PostStatusType status = PostStatusType.PROCEEDING;


        return Post.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .topic(topic)
                .status(status)
                .postImgs(new ArrayList<>())
                .location(location)
                .build();
    }

    public static PostResponseDTO.LocationListDTO toLocationList(List<Location> locationList){
        List<PostResponseDTO.LocationDTO> locationListDTO= locationList.stream().map(location ->
            PostResponseDTO.LocationDTO.builder()
                    .id(location.getId())
                    .address(location.getAddress())
                    .build()).collect(Collectors.toList());

        return PostResponseDTO.LocationListDTO.builder()
                .LocationList(locationListDTO)
                .build();
    }

    public static PostResponseDTO.PostListDTO toPostList(List<Post> postList, Member member) {
        List<PostResponseDTO.PostDTO> postDTOS = postList.stream().map(post -> {
            List<PostResponseDTO.PostImgResponseDTO> imgs = post.getPostImgs().stream().map(img ->
                            PostResponseDTO.PostImgResponseDTO.builder()
                                    .itemImgUrl(img.getUrl())
                                    .build())
                    .collect(Collectors.toList());

            return PostResponseDTO.PostDTO.builder()
                    .id(post.getId())
                    .address(post.getLocation().getAddress())
                    .title(post.getTitle())
                    .status(post.getStatus())
                    .content(post.getContent())
                    .nickname(member.getNickname())
                    .createdAt(post.getCreatedAt())
                    .itemImgUrlList(imgs)
                    .build();
        }).collect(Collectors.toList());

        return PostResponseDTO.PostListDTO.builder()
                .postDTOList(postDTOS)
                .build();
    }

    public static PostResponseDTO.PostDetailDTO toPostDTO(Post post, Member member){

        List<PostResponseDTO.PostImgResponseDTO> imgs = post.getPostImgs().stream().map(img ->
                        PostResponseDTO.PostImgResponseDTO.builder()
                                .itemImgUrl(img.getUrl())
                                .build())
                .collect(Collectors.toList());

        return PostResponseDTO.PostDetailDTO.builder()
                .id(post.getId())
                .address(post.getLocation().getAddress())
                .title(post.getTitle())
                .status(post.getStatus())
                .content(post.getContent())
                .nickname(member.getNickname())
                .createdAt(post.getCreatedAt())
                .itemImgUrlList(imgs)
                .build();
    }

    public static PostImg toPostImg(String imgUrl, MultipartFile multipartFile){
        return PostImg.builder()
                .url(imgUrl)
                .name(multipartFile.getOriginalFilename())
                .build();
    }

    public static Location toLocation(PostRequestDTO.LocationDTO request, NaverGeoResponse naverGeoResponse, Member member){
        return Location.builder()
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .address(naverGeoResponse.getResults().get(0).getRegion().getArea3().getName())
                .member(member)
                .build();

    }


}





