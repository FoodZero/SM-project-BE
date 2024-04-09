package com.sm.project.converter.member;

import com.sm.project.domain.enums.JoinType;
import com.sm.project.domain.member.Member;
import com.sm.project.domain.member.MemberPassword;
import com.sm.project.web.dto.member.MemberRequestDTO;
import com.sm.project.web.dto.member.MemberResponseDTO;

import java.util.ArrayList;


public class MemberConverter {

    public static MemberResponseDTO.SocialJoinResultDTO toSocialJoinResultDTO(String phone, String email) {

        return MemberResponseDTO.SocialJoinResultDTO.builder()
                .phone(phone)
                .email(email)
                .build();
    }

    public static Member toMember(MemberRequestDTO.JoinDTO request) {
        return Member.builder()
                .email(request.getEmail())
                .nickname(request.getNickname())
                .phone(request.getPhone())
                .infoAgree(request.getInfoAgree())
                .messageAgree(request.getMessageAgree())
                .fcmTokenList(new ArrayList<>())
                .joinType(JoinType.GENERAL)
                .build();
    }

    public static MemberPassword toMemberPassword(String password, Member member) {
        return MemberPassword.builder()
                .password(password)
                .member(member)
                .build();
    }

    public static MemberResponseDTO.JoinResultDTO toJoinResultDTO(Member member) {
        return MemberResponseDTO.JoinResultDTO.builder()
                .memberId(member.getId())
                .build();
    }

    public static MemberResponseDTO.EmailResultDTO toEmailResultDTO(String email) {
        return MemberResponseDTO.EmailResultDTO.builder()
                .email(email)
                .build();
    }
}
