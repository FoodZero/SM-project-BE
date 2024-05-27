package com.sm.project.converter.community;

import com.sm.project.domain.community.Post;
import com.sm.project.domain.member.Member;
import com.sm.project.web.dto.community.PostRequestDTO;

public class PostConverter {

    public static Post toPost(Member member, PostRequestDTO.CreateDTO request) {

        return Post.builder()
                .member(member)
                .content(request.getContent())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .build();
    }



}
