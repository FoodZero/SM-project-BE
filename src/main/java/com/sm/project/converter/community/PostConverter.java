package com.sm.project.converter.community;

import com.sm.project.domain.community.Post;
import com.sm.project.domain.member.Member;
import com.sm.project.web.dto.community.PostRequestDTO;
import com.sm.project.domain.enums.PostStatusType;
import com.sm.project.domain.enums.PostTopicType;
import com.sm.project.domain.member.Location;
import com.sm.project.domain.member.Member;
import com.sm.project.web.dto.community.PostRequestDTO;
import com.sm.project.web.dto.community.PostResponseDTO;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



public class PostConverter {

    public static Post toPost(Member member, PostRequestDTO.CreateDTO request) {
        PostTopicType topic;
        PostStatusType status;

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

        if (request.getStatus().equals("마감")) {
            status = PostStatusType.END;
        }
        else status = PostStatusType.PROCEEDING;

        return Post.builder()
                .member(member)
                .title(request.getTitle())
                .content(request.getContent())
                .topic(topic)
                .status(status)
                .postImgs(new ArrayList<>())
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


}
