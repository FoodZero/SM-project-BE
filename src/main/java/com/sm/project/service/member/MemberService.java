package com.sm.project.service.member;

import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.config.springSecurity.TokenProvider;
import com.sm.project.converter.member.MemberConverter;
import com.sm.project.coolsms.RedisUtil;
import com.sm.project.coolsms.SmsUtil;
import com.sm.project.domain.enums.StatusType;
import com.sm.project.domain.food.Refrigerator;
import com.sm.project.domain.mapping.MemberRefrigerator;
import com.sm.project.domain.member.FcmRepository;
import com.sm.project.domain.member.FcmToken;
import com.sm.project.domain.member.Member;
import com.sm.project.domain.member.MemberPassword;
import com.sm.project.feignClient.dto.KakaoProfile;
import com.sm.project.feignClient.dto.KakaoTokenResponse;
import com.sm.project.feignClient.kakao.KakaoTokenFeignClient;
import com.sm.project.feignClient.service.KakaoOauthService;
import com.sm.project.firebase.FcmService;
import com.sm.project.redis.service.RedisService;
import com.sm.project.repository.food.FoodRepository;
import com.sm.project.repository.food.RefrigeratorRepository;
import com.sm.project.repository.member.MemberPasswordRepository;
import com.sm.project.repository.member.MemberRefrigeratorRepository;
import com.sm.project.repository.member.MemberRepository;
import com.sm.project.service.mail.MailService;
import com.sm.project.web.dto.member.MemberRequestDTO;
import com.sm.project.web.dto.member.MemberResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * MemberService는 회원 관련 기능을 제공하는 서비스 클래스입니다.
 * 로그인, 회원가입, 비밀번호 재설정, 카카오 로그인, 푸시 알림 등 다양한 기능을 제공합니다.
 */
@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
@EnableScheduling
public class MemberService {

    private final RedisService redisService;
    private final TokenProvider tokenProvider;
    private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder encoder;
    private final MemberPasswordRepository memberPasswordRepository;
    private final KakaoOauthService kakaoOauthService;
    private final KakaoTokenFeignClient tokenClient;
    private final FoodRepository foodRepository;
    private final FcmService fcmService;
    private final SmsUtil smsUtil;
    private final RedisUtil redisUtil;
    private final MemberQueryService memberQueryService;
    private final MailService mailService;
    private final FcmRepository fcmRepository;
    private final RefrigeratorRepository refrigeratorRepository;
    private final MemberRefrigeratorRepository memberRefrigeratorRepository;

    @Value("${oauth2.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth2.kakao.redirect-uri}")
    private String kakaoRedirectUri;

    /**
     * 로그인 메서드
     * 
     * @param request 로그인 요청 데이터
     * @return 로그인 결과를 포함한 DTO
     */
    public MemberResponseDTO.LoginDTO login(MemberRequestDTO.LoginDTO request) {
        Member selectedMember = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_EMAIL_NOT_FOUND));
        if(selectedMember.getStatus() == StatusType.INACTIVE){
            throw new MemberHandler(ErrorStatus.MEMBER_EMAIL_NOT_FOUND);
        }
        MemberPassword memberPassword = memberPasswordRepository.findByMember(selectedMember);
        if (!encoder.matches(request.getPassword(), memberPassword.getPassword())) {
            throw new MemberHandler(ErrorStatus.MEMBER_PASSWORD_ERROR);
        }

        return MemberResponseDTO.LoginDTO.builder()
                .accessToken(redisService.saveLoginStatus(selectedMember.getId(), tokenProvider.createAccessToken(selectedMember.getId(), selectedMember.getJoinType(), request.getEmail(), Arrays.asList(new SimpleGrantedAuthority("USER")))))
                .refreshToken(redisService.generateRefreshToken(request.getEmail()).getToken())
                .build();
    }

    /**
     * 로그 아웃 메서드
     * 로그인 상태 정보 삭제
     * @param accessToken
     */
    @Transactional
    public void logout(String accessToken) {
        redisService.resolveLogout(accessToken);
    }

    /**
     * 카카오 로그인 정보를 가져오는 메서드
     * 
     * @param code 카카오 인증 코드
     * @return 카카오 로그인 결과를 포함한 응답 DTO
     */
    @Transactional
    public ResponseDTO<?> getKakaoInfo(String code) {
        KakaoTokenResponse token = tokenClient.generateToken("authorization_code", kakaoClientId, kakaoRedirectUri, code);
        KakaoProfile kakaoProfile = kakaoOauthService.getKakaoUserInfo("Bearer " + token.getAccess_token());

        String email = kakaoProfile.getKakao_account().getEmail();
        Optional<Member> member = memberRepository.findByEmail(email);

        String phone = kakaoProfile.getKakao_account().getPhone_number().replaceAll("[^0-9]", "");
        if (phone.startsWith("82")) {
            phone = "0" + phone.substring(2);
        }

        if (member.isEmpty()) {
            return ResponseDTO.onFailure("로그인 실패", "회원가입 필요", MemberConverter.toSocialJoinResultDTO(phone, email));
        } else {
            if(member.get().getStatus() == StatusType.INACTIVE){
                return ResponseDTO.onFailure("로그인 실패", "회원가입 필요", MemberConverter.toSocialJoinResultDTO(phone, email));
            }
            return ResponseDTO.onSuccess(MemberResponseDTO.LoginDTO.builder()
                    .accessToken(redisService.saveLoginStatus(member.get().getId(), tokenProvider.createAccessToken(member.get().getId(), member.get().getJoinType(), email, Arrays.asList(new SimpleGrantedAuthority("USER")))))
                    .refreshToken(redisService.generateRefreshToken(email).getToken())
                    .build());
        }
    }

    /**
     * 회원가입 메서드
     * 
     * @param request 회원가입 요청 데이터
     * @return 새로 가입된 회원 객체
     */
    @Transactional
    public Member joinMember(MemberRequestDTO.JoinDTO request) {
        //verifySms(request.getPhone(), request.getCertificationCode());
        Optional<Member> member = memberRepository.findByEmail(request.getEmail());


        if (member.isPresent()) {
            if(member.get().getStatus() == StatusType.INACTIVE){
                memberRepository.delete(member.get());

            }else{
                throw new MemberHandler(ErrorStatus.MEMBER_ALREADY_JOIN);
            }

        }

        Member newMember = MemberConverter.toMember(request);
        memberRepository.save(newMember);

        FcmToken fcmToken = FcmToken.builder()
                .token(request.getFcmToken())
                .build();
        fcmToken.setMember(newMember);
        fcmRepository.save(fcmToken);

        String password = encoder.encode(request.getPassword());
        memberPasswordRepository.save(MemberConverter.toMemberPassword(password, newMember));
        return newMember;
    }

    /**
     * 회원 삭제 메서드
     * @param member
     */
    public void deleteMember(Member member){
        member.deleteMember();
    }

    /**
     * 닉네임 중복 확인 메서드
     * 
     * @param request 닉네임 중복 확인 요청 데이터
     * @return 중복 여부
     */
    public boolean isDuplicate(MemberRequestDTO.NicknameDTO request) {
        Member member = memberRepository.findByNickname(request.getNickname());
        return (member != null);
    }

    /**
     * 푸시 알림 전송 메서드
     * 
     * @throws IOException 예외 발생 시
     */
    //@Scheduled(cron = "0 0 17 * * ?")
    //@Scheduled(cron = "0 0/1 * * * ?")
    public void sendPushAlarm() throws IOException {
        List<Refrigerator> refrigeratorList = refrigeratorRepository.findAll();
        Map<String, Integer> map = new HashMap<>();
        Date currentTime = new Date();

        refrigeratorList.forEach(refrigerator -> {
            // foodRepository에서 음식 목록 가져오기
            foodRepository.findTop5ByRefrigeratorOrderByExpireDesc(refrigerator).forEach(food -> {
                // 식품 이름과 남은 유통기한을 map에 저장
                int daysRemaining = (int) ((food.getExpire().getTime() - currentTime.getTime()) / (1000 * 60 * 60 * 24));
                map.put(food.getName(), daysRemaining);
            });

            try {
                // 유통기한 정보 메시지 생성
                String result = map.entrySet().stream()
                        .map(entry -> entry.getKey() + "의 유통기한: " + entry.getValue() + "일 남음")
                        .collect(Collectors.joining("\n"));

                // Refrigerator와 연관된 MemberRefrigerator 목록 조회
                List<MemberRefrigerator> memberRefrigeratorList = memberRefrigeratorRepository.findByRefrigerator(refrigerator);

                for (MemberRefrigerator memberRefrigerator : memberRefrigeratorList) {
                    // MemberRefrigerator를 이용해 Member 조회
                    Member member = memberRepository.findByMemberRefrigeratorListContaining(memberRefrigerator);
                    if (member != null && !member.getFcmTokenList().isEmpty()) {
                        fcmService.sendMessage(
                                member.getFcmTokenList().get(0).getToken(),
                                "유통기한이 곧 지나는 식품들입니다.",
                                result
                        );
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * SMS 전송 메서드
     * 
     * @param smsDTO SMS 전송 요청 데이터
     */
    public void sendSms(MemberRequestDTO.SmsDTO smsDTO) {
        String to = smsDTO.getPhone();
        int randomNum = (int) (Math.random() * 9000) + 1000;
        String vertificationCode = String.valueOf(randomNum);
        smsUtil.sendOne(to, vertificationCode);
        redisUtil.createSmsCertification(to, vertificationCode);
    }

    /**
     * SMS 인증 코드 검증 메서드
     * 
     * @param phone 전화번호
     * @param certificationCode 인증 코드
     */
    public void verifySms(String phone, String certificationCode) {
        if (isVerifySms(phone, certificationCode)) {
            throw new MemberHandler(ErrorStatus.MEMBER_VERIFY_FAILURE);
        }
        redisUtil.removeSmsCertification(phone);
    }

    /**
     * SMS 인증 코드 검증 여부 확인 메서드
     * 
     * @param phone 전화번호
     * @param certificationCode 인증 코드
     * @return 검증 여부
     */
    public boolean isVerifySms(String phone, String certificationCode) {
        return !(redisUtil.hasKey(phone) && redisUtil.getSmsCertification(phone).equals(certificationCode));
    }

     /**
     * 이메일 전송 메서드
     * 
     * 가입된 이메일인지 확인하고, 비밀번호 재설정을 위한 인증 코드를 생성하여 이메일로 전송합니다.
     * 
     * @param request 이메일 전송 요청 데이터
     * @throws MessagingException 예외 발생 시
     * @throws UnsupportedEncodingException 예외 발생 시
     */
    @Transactional
    public void sendEmail(MemberRequestDTO.SendEmailDTO request) throws MessagingException, UnsupportedEncodingException {
        Member member = memberQueryService.findByEmail(request.getEmail()); // 가입된 메일인지 검사. null이면 에러 발생

        // 랜덤 수 생성
        int randomNum = (int) (Math.random() * 9000) + 1000;
        String vertificationCode = String.valueOf(randomNum);
        redisUtil.createEmailCertification(request.getEmail(), vertificationCode); // redis에 key:이메일, value:인증코드 저장

        mailService.sendResetPwdEmail(member.getEmail(), vertificationCode);
    }

    /**
     * 이메일 인증 코드 검증 후 삭제 메서드
     * 
     * @param email 이메일 주소
     * @param certificationCode 인증 코드
     */
    public void verifyEmail(String email, String certificationCode) {
        if (isVerifyEmail(email, certificationCode)) {
            throw new MemberHandler(ErrorStatus.MEMBER_VERIFY_FAILURE);
        }
        redisUtil.removeEmailCertification(email);
    }

    /**
     * 이메일 인증 코드 검증 메서드
     * 
     * @param email 이메일 주소
     * @param certificationCode 인증 코드
     * @return 인증 코드 검증 결과
     */
    public boolean isVerifyEmail(String email, String certificationCode) {
        return !(redisUtil.hasKeyEmail(email) && redisUtil.getEmailCertification(email).equals(certificationCode));
    }

    /**
     * 비밀번호 재설정 메서드
     * 
     * 사용자가 입력한 새 비밀번호와 비밀번호 확인이 일치하는지 확인하고, 일치하면 비밀번호를 재설정합니다.
     * 
     * @param request 비밀번호 재설정 요청 데이터
     */
    @Transactional
    public void resetPassword(MemberRequestDTO.PasswordDTO request) {
        Member member = memberQueryService.findByEmail(request.getEmail());

        if (request.getNewPassword().equals(request.getPasswordCheck())) { // 새 비밀번호가 일치하는지 확인
            member.getMemberPassword().setPassword(encoder.encode(request.getNewPassword()));
        } else {
            throw new MemberHandler(ErrorStatus.MEMBER_PASSWORD_MISMATCH);
        }
    }

    /**
     * 닉네임 변경 메서드
     * @param member
     * @param request
     */
    public void updateNickname(Member member, MemberRequestDTO.NicknameDTO request){
        memberRepository.updateMemberName(member.getId(), request.getNickname());
    }






}
