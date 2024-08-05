package com.sm.project.service.member;

import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.config.springSecurity.TokenProvider;
import com.sm.project.converter.member.MemberConverter;
import com.sm.project.coolsms.RedisUtil;
import com.sm.project.domain.member.FcmRepository;
import com.sm.project.domain.member.FcmToken;
import com.sm.project.domain.member.Member;
import com.sm.project.domain.member.MemberPassword;
import com.sm.project.feignClient.dto.KakaoProfile;
import com.sm.project.feignClient.dto.KakaoTokenResponse;
import com.sm.project.feignClient.kakao.KakaoTokenFeignClient;
import com.sm.project.feignClient.service.KakaoOauthService;
import com.sm.project.redis.domain.RefreshToken;
import com.sm.project.redis.service.RedisService;
import com.sm.project.repository.member.MemberPasswordRepository;
import com.sm.project.repository.member.MemberRepository;
import com.sm.project.web.dto.member.MemberRequestDTO;
import com.sm.project.web.dto.member.MemberResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;


@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private RedisService redisService;
    @Mock
    private TokenProvider tokenProvider;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private MemberPasswordRepository memberPasswordRepository;

    @Mock
    private KakaoOauthService kakaoOauthService;

    @Mock
    private KakaoTokenFeignClient tokenClient;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private FcmRepository fcmRepository;

    @Mock
    private RedisTemplate<String, String> redisTemplate;


    @InjectMocks
    private MemberService memberService;

    @Value("${oauth2.kakao.client-id}")
    private String kakaoClientId;

    @Value("${oauth2.kakao.redirect-uri}")
    private String kakaoRedirectUri;

     private Member member;
     private MemberPassword memberPassword;
     private String email;
     private String password;
     private String encodedPassword;
     private MemberRequestDTO.LoginDTO request;
     private String accessToken;
     private String refresh;
     private RefreshToken refreshToken;
     private MemberRequestDTO.JoinDTO joinRequest;

    @BeforeEach
    public void setUp(){
        email = "test";
        password = "1234";
        request = new MemberRequestDTO.LoginDTO(email, password);

        member = Member.builder()
                .id(1L)
                .email(email)
                .build();

        encodedPassword = new BCryptPasswordEncoder().encode(password);
        memberPassword = MemberPassword.builder().build();
        memberPassword.setPassword(encodedPassword);

        accessToken = "accessToken";
        refresh = "refreshToken";
        LocalDateTime localDateTime = LocalDateTime.now();
        refreshToken = RefreshToken.builder()
                .token(refresh)
                .memberId(member.getId())
                .expireTime(localDateTime.plusHours(12))
                .build();

        joinRequest = MemberRequestDTO.JoinDTO.builder()
                .email("test@example.com")
                .phone("01012345678")
                .certificationCode("1234")
                .password("password")
                .nickname("testNickname")
                .fcmToken("fcmToken")
                .infoAgree(true)
                .messageAgree(true)
                .build();

    }
    @Test
    @DisplayName("로그인 테스트")
    void testLoginSuccess() {
        //given
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(memberPasswordRepository.findByMember(member)).willReturn(memberPassword);
        given(encoder.matches(password, memberPassword.getPassword())).willReturn(true);
        given(redisService.saveLoginStatus(eq(member.getId()), any())).willReturn("accessToken");
        given(redisService.generateRefreshToken(email)).willReturn(refreshToken);

        //when
        MemberResponseDTO.LoginDTO result = memberService.login(request);

        //then
        assertThat(result).isNotNull();
        assertThat(accessToken).isEqualTo(result.getAccessToken());
        assertThat(accessToken).isEqualTo(result.getAccessToken());
        assertThat(refreshToken.getToken()).isEqualTo(result.getRefreshToken());
    }
    @Test
    @DisplayName("로그인 실패 테스트 - 이메일 없음")
    void testLoginFailure_EmailNotFound() {
        //given
        given(memberRepository.findByEmail(email)).willReturn(Optional.empty());

        //when & then
        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(MemberHandler.class);

    }

    @Test
    @DisplayName("로그인 실패 테스트 - 비밀번호 불일치")
    void testLoginFailure_IncorrectPassword() {
        //given
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(memberPasswordRepository.findByMember(member)).willReturn(memberPassword);
        given(encoder.matches(password, memberPassword.getPassword())).willReturn(false);

        //when & then
        assertThatThrownBy(() -> memberService.login(request))
                .isInstanceOf(MemberHandler.class);
    }

    @Test
    @DisplayName("카카오 로그인 정보 가져오기 테스트")
    void testGetKakaoInfoSuccess() {
        // given
        String code = "authCode";
        String kakaoAccessToken = "kakaoAccessToken";
        KakaoTokenResponse tokenResponse = new KakaoTokenResponse();
        tokenResponse.setAccess_token(kakaoAccessToken);

        KakaoProfile kakaoProfile = new KakaoProfile();
        KakaoProfile.KakaoAccount account = kakaoProfile.new KakaoAccount();
        account.setEmail(email);
        account.setPhone_number("821012345678");
        kakaoProfile.setKakao_account(account);

        given(tokenClient.generateToken("authorization_code", kakaoClientId, kakaoRedirectUri, code)).willReturn(tokenResponse);
        given(kakaoOauthService.getKakaoUserInfo("Bearer " + kakaoAccessToken)).willReturn(kakaoProfile);
        given(memberRepository.findByEmail(email)).willReturn(Optional.of(member));
        given(redisService.saveLoginStatus(eq(member.getId()), any())).willReturn(accessToken);
        given(redisService.generateRefreshToken(email)).willReturn(refreshToken);

        // when
        ResponseDTO<?> response = memberService.getKakaoInfo(code);
        // then
        assertThat(response).isNotNull();
        assertThat(response.getIsSuccess()).isTrue();
        MemberResponseDTO.LoginDTO loginDTO = (MemberResponseDTO.LoginDTO) response.getResult();
        assertThat(loginDTO.getAccessToken()).isEqualTo(accessToken);
        assertThat(loginDTO.getRefreshToken()).isEqualTo(refreshToken.getToken());
    }

    @Test
    @DisplayName("회원가입 테스트")
    void testJoinMemberSuccess() {
        //given
        given(redisUtil.hasKey(joinRequest.getPhone())).willReturn(true);
        given(redisUtil.getSmsCertification(joinRequest.getPhone())).willReturn(joinRequest.getCertificationCode());


        Member newMember = MemberConverter.toMember(joinRequest);
        FcmToken fcmToken = FcmToken.builder().token(joinRequest.getFcmToken()).build();
        fcmToken.setMember(newMember);
        List<FcmToken> fcmTokenList = new ArrayList<>();
        fcmTokenList.add(fcmToken);

        // when
        Member result = memberService.joinMember(joinRequest);
        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(joinRequest.getEmail());
        assertThat(result.getFcmTokenList().get(0).getToken()).isEqualTo(joinRequest.getFcmToken());



    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void testJoinMemberFailure_EmailAlreadyExists() {
        // given
        Member existingMember = Member.builder()
                .id(1L)
                .email(joinRequest.getEmail())
                .build();

        lenient().when(memberRepository.findByEmail(joinRequest.getEmail())).thenReturn(Optional.of(existingMember));

        // when & then
        assertThatThrownBy(() -> memberService.joinMember(joinRequest))
                .isInstanceOf(MemberHandler.class);



    }


}
