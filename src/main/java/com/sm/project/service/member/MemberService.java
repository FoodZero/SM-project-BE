package com.sm.project.service.member;

import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.config.springSecurity.TokenProvider;
import com.sm.project.converter.member.MemberConverter;
import com.sm.project.coolsms.RedisUtil;
import com.sm.project.coolsms.SmsUtil;
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
import com.sm.project.repository.member.MemberPasswordRepository;
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


    @Value("${oauth2.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth2.kakao.redirect-uri}")
    private String kakaoRedirectUri;


    public MemberResponseDTO.LoginDTO login(MemberRequestDTO.LoginDTO request) {

        //email 없음
        Member selectedMember = memberRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new MemberHandler(ErrorStatus.MEMBER_EMAIL_NOT_FOUND));

        MemberPassword memberPassword = memberPasswordRepository.findByMember(selectedMember);
        //password 틀림
        if (!encoder.matches(request.getPassword(), memberPassword.getPassword())) {
            throw new MemberHandler(ErrorStatus.MEMBER_PASSWORD_ERROR);
        }


        return MemberResponseDTO.LoginDTO.builder()
                .accessToken(redisService.saveLoginStatus(selectedMember.getId(), tokenProvider.createAccessToken(selectedMember.getId(), selectedMember.getJoinType(), request.getEmail(), Arrays.asList(new SimpleGrantedAuthority("USER")))))
                .refreshToken(redisService.generateRefreshToken(request.getEmail()).getToken())
                .build();

    }

    @Transactional
    public ResponseDTO<?> getKakaoInfo(String code) {
        KakaoTokenResponse token = tokenClient.generateToken("authorization_code",kakaoClientId, kakaoRedirectUri, code);


        KakaoProfile kakaoProfile = kakaoOauthService.getKakaoUserInfo("Bearer "+ token.getAccess_token());

        String email = kakaoProfile.getKakao_account().getEmail();

        Optional<Member> member = memberRepository.findByEmail(email);

        String phone = kakaoProfile.kakao_account.phone_number.replaceAll("[^0-9]", "");
        if (phone.startsWith("82")) {
            phone = "0" + phone.substring(2);
        }

        if(member.isEmpty()){
            return ResponseDTO.onFailure("로그인 실패", "회원가입 필요", MemberConverter.toSocialJoinResultDTO(phone, email));
        }
        else{
            return ResponseDTO.onSuccess(MemberResponseDTO.LoginDTO.builder()
                    .accessToken(redisService.saveLoginStatus(member.get().getId(), tokenProvider.createAccessToken(member.get().getId(), member.get().getJoinType() ,email, Arrays.asList(new SimpleGrantedAuthority("USER")))))
                    .refreshToken(redisService.generateRefreshToken(email).getToken())
                    .build());
        }
    }

    @Transactional
    public Member joinMember(MemberRequestDTO.JoinDTO request) {
        verifySms(request.getPhone(), request.getCertificationCode()); //인증코드 검사

        if (memberRepository.findByEmail(request.getEmail()).isPresent()) { //이메일 중복 검사
            throw new MemberHandler(ErrorStatus.MEMBER_ALREADY_JOIN);
        }

        Member newMember = MemberConverter.toMember(request);
        memberRepository.save(newMember);

        FcmToken fcmToken = FcmToken.builder()
                .token(request.getFcmToken())
                .serialNumber(request.getSerialNumber())
                .build();

        fcmToken.setMember(newMember);

        fcmRepository.save(fcmToken);

        //패스워드 해시처리.
        String password = encoder.encode(request.getPassword());
        memberPasswordRepository.save(MemberConverter.toMemberPassword(password, newMember));
        return newMember;
    }

    public boolean isDuplicate(MemberRequestDTO.NicknameDTO request) {
        Member member = memberRepository.findByNickname(request.getNickname());
        return (member!=null);
    }
    //@Scheduled(cron = "0 0 17 * * ?")
    //
    //@Scheduled(cron = "0 0/1 * * * ?")
    public void sendPushAlarm() throws IOException {
        List<Member> memberList = memberRepository.findAll();
        Map<String,Integer> map = new HashMap<>();
        Date currentTime = new Date();

        memberList.stream().forEach(member -> {
            foodRepository.findTop5ByMemberOrderByExpireDesc(member).stream().forEach(food -> {
                map.put(food.getName(), (int) (currentTime.getTime() - food.getExpire().getTime()));
            });
            try {
                String result = map.entrySet().stream()
                        .map(entry -> entry.getKey() + "의 유통기한: " + entry.getValue() + "일 남음")
                        .collect(Collectors.joining("\n"));
                fcmService.sendMessage(member.getFcmTokenList().get(0).getToken(),"유통기한이 곧 지나는 식품들입니다.",result);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });



    }



    public void sendSms(MemberRequestDTO.SmsDTO smsDTO) {
        String to = smsDTO.getPhone();
        int randomNum = (int) (Math.random()*9000)+1000;
        String vertificationCode = String.valueOf(randomNum);
        smsUtil.sendOne(to, vertificationCode); //인증문자 전송

        //redis에 저장하는 코드
        redisUtil.createSmsCertification(to, vertificationCode);
    }

    public void verifySms(String phone, String certificationCode) {
        if (isVerifySms(phone, certificationCode)) {
            throw new MemberHandler(ErrorStatus.MEMBER_VERIFY_FAILURE);
        }
        redisUtil.removeSmsCertification(phone);
    }

    public boolean isVerifySms(String phone, String certificationCode) {
        return !(redisUtil.hasKey(phone) && redisUtil.getSmsCertification(phone).equals(certificationCode));
    }

    @Transactional
    public void sendEmail(MemberRequestDTO.SendEmailDTO request) throws MessagingException, UnsupportedEncodingException {
        Member member = memberQueryService.findByEmail(request.getEmail()); //가입된 메일인지 검사. null이면 에러발생

        //랜덤 수 생성
        int randomNum = (int) (Math.random()*9000)+1000;
        String vertificationCode = String.valueOf(randomNum);
        redisUtil.createEmailCertification(request.getEmail(), vertificationCode); //redis에 key:이메일, value:인증코드 저장

        mailService.sendResetPwdEmail(member.getEmail(), vertificationCode);
    }

    public void verifyEmail(String email, String certificationCode) {  //인증 코드 검증 후 삭제
        if (isVerifyEmail(email, certificationCode)) {
            throw new MemberHandler(ErrorStatus.MEMBER_VERIFY_FAILURE);
        }
        redisUtil.removeEmailCertification(email);
    }

    public boolean isVerifyEmail(String email, String certificationCode) {  //이메일 인증 코드 검증
        return !(redisUtil.hasKeyEmail(email) && redisUtil.getEmailCertification(email).equals(certificationCode));
    }

    @Transactional
    public void resetPassword(MemberRequestDTO.PasswordDTO request) {
        Member member = memberQueryService.findByEmail(request.getEmail());

        if (request.getNewPassword().equals(request.getPasswordCheck())) { //새비밀번호 일치한지 확인
            member.getMemberPassword().setPassword(encoder.encode(request.getNewPassword()));
        } else {
            throw new MemberHandler(ErrorStatus.MEMBER_PASSWORD_MISMATCH);
        }
    }



}
