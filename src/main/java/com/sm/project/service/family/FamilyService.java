package com.sm.project.service.family;

import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.FamilyHandler;
import com.sm.project.coolsms.RedisUtil;
import com.sm.project.domain.family.Family;
import com.sm.project.domain.member.Member;
import com.sm.project.repository.family.FamilyRepository;
import com.sm.project.repository.member.MemberRepository;
import com.sm.project.service.mail.MailService;
import com.sm.project.web.dto.family.FamilyRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

/**
 * FamilyService는 가족과 관련된 비즈니스 로직을 처리하는 서비스 클래스입니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FamilyService {

    // 의존성 주입을 통해 필요한 서비스와 리포지토리를 사용합니다.
    private final MailService mailService;
    private final MemberRepository memberRepository;
    private final FamilyRepository familyRepository;
    private final RedisUtil redisUtil;

    /**
     * 주어진 이메일 주소로 인증 코드를 생성하여 발송하는 메서드입니다.
     *
     * @param email 인증 코드를 발송할 이메일 주소
     */
    public void sendVerificationCode(String email) {
        // 1000에서 9999 사이의 난수를 생성하여 인증 코드로 사용
        int randomNum = (int) (Math.random() * 9000) + 1000;
        String verificationCode = String.valueOf(randomNum);
        try {
            // 이메일로 인증 코드 발송
            mailService.sendVerificationCode(email, verificationCode);
            // Redis에 이메일과 인증 코드를 저장
            redisUtil.createEmailCertification(email, verificationCode);
        } catch (MessagingException | UnsupportedEncodingException e) {
            // 이메일 발송 실패 시 예외 처리
            throw new FamilyHandler(ErrorStatus.MEMBER_VERIFY_FAILURE);
        }
    }

    /**
     * 이메일과 인증 코드를 검증하고, 검증이 성공하면 가족 정보로 등록하는 메서드입니다.
     *
     * @param request 인증 요청 데이터 (이메일, 인증 코드 포함)
     */
    public void verifyAndRegisterFamily(FamilyRequestDTO.VerificationDTO request) {
        // Redis에서 이메일에 대한 인증 코드가 존재하는지 확인하고, 일치하는지 검증
        if (!redisUtil.hasKeyEmail(request.getEmail()) ||
            !redisUtil.getEmailCertification(request.getEmail()).equals(request.getVerificationCode())) {
            // 검증 실패 시 예외 처리
            throw new FamilyHandler(ErrorStatus.MEMBER_VERIFY_FAILURE);
        }
        // 인증이 성공하면 Redis에서 해당 인증 코드를 삭제
        redisUtil.removeEmailCertification(request.getEmail());

        // 이메일로 회원 정보를 조회
        Optional<Member> memberOpt = memberRepository.findByEmail(request.getEmail());
        if (memberOpt.isPresent()) {
            // 회원이 존재하면 가족 정보를 생성하고 저장
            Member member = memberOpt.get();
            Family family = Family.builder()
                                  .member(member)
                                  .build();
            familyRepository.save(family);
        } else {
            // 회원이 존재하지 않을 경우 예외 처리
            throw new FamilyHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }
    }
}