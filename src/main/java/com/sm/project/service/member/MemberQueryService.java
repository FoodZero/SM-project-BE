package com.sm.project.service.member;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.domain.member.Member;
import com.sm.project.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sm.project.web.dto.member.MemberRequestDTO;

import java.util.Optional;

/**
 * MemberQueryService는 회원 정보 조회 관련 기능을 제공하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberQueryService {

    private final MemberRepository memberRepository;

    /**
     * ID를 통해 회원을 조회하는 메서드입니다.
     *
     * @param id 회원 ID
     * @return 회원 객체 (Optional)
     */
    public Optional<Member> findMemberById(Long id) {
        return memberRepository.findById(id);
    }

    /**
     * 전화번호를 통해 회원의 이메일을 조회하는 메서드입니다.
     *
     * @param phone 회원 전화번호
     * @return 회원 객체
     * @throws MemberHandler 회원을 찾을 수 없는 경우 예외 발생
     */
    public Member findEmail(String phone) {
        return memberRepository.findByPhone(phone).orElseThrow(
                () -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

    /**
     * 이메일을 통해 회원을 조회하는 메서드입니다.
     *
     * @param email 회원 이메일
     * @return 회원 객체
     * @throws MemberHandler 회원을 찾을 수 없는 경우 예외 발생
     */
    public Member findByEmail(String email) {
        return memberRepository.findByEmail(email).orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_NOT_FOUND));
    }

}
