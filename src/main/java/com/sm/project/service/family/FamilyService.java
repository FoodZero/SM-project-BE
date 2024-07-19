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

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class FamilyService {

    private final MailService mailService;
    private final MemberRepository memberRepository;
    private final FamilyRepository familyRepository;
    private final RedisUtil redisUtil;

    // 이메일로 인증 코드를 발송하는 메서드
    public void sendVerificationCode(String email) {
        Optional<Member> member = memberRepository.findByEmail(email);
        if (member.isEmpty()) {
            throw new FamilyHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }
        int randomNum = (int) (Math.random() * 9000) + 1000;
        String verificationCode = String.valueOf(randomNum);
        try {
            mailService.sendVerificationCode(email, verificationCode);
            redisUtil.createEmailCertification(email, verificationCode);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new FamilyHandler(ErrorStatus.MEMBER_VERIFY_FAILURE);
        }
    }

    // 이메일과 인증 코드를 검증하고 패밀리에 등록하는 메서드
    public void verifyAndRegisterFamily(FamilyRequestDTO.VerificationDTO request) {
        if (!redisUtil.hasKeyEmail(request.getEmail()) ||
            !redisUtil.getEmailCertification(request.getEmail()).equals(request.getVerificationCode())) {
            throw new FamilyHandler(ErrorStatus.MEMBER_VERIFY_FAILURE);
        }
        redisUtil.removeEmailCertification(request.getEmail());
        Optional<Member> memberOpt = memberRepository.findByEmail(request.getEmail());
        if (memberOpt.isPresent()) {
            Member member = memberOpt.get();
            Family family = Family.builder()
                                  .member(member)
                                  .build();
            familyRepository.save(family);
        } else {
            throw new FamilyHandler(ErrorStatus.MEMBER_NOT_FOUND);
        }
    }
}
