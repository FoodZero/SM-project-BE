package com.sm.project.web.controller.member;

import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.apiPayload.code.ErrorReasonDTO;
import com.sm.project.apiPayload.code.status.ErrorStatus;
import com.sm.project.apiPayload.code.status.SuccessStatus;
import com.sm.project.apiPayload.exception.handler.MemberHandler;
import com.sm.project.converter.member.MemberConverter;
import com.sm.project.domain.member.Member;
import com.sm.project.service.UtilService;
import com.sm.project.service.family.FamilyService;
import com.sm.project.service.member.MemberQueryService;
import com.sm.project.service.member.MemberService;
import com.sm.project.web.dto.family.FamilyRequestDTO;
import com.sm.project.web.dto.member.MemberRequestDTO;
import com.sm.project.web.dto.member.MemberResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * MemberController는 회원 관련 API 요청을 처리하는 컨트롤러 클래스입니다.
 * 회원가입, 로그인, 이메일 찾기, 비밀번호 재설정 등의 기능을 제공합니다.
 */
@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Member", description = "Member 관련 API")
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberQueryService memberQueryService;
    private final UtilService utilService;

    /**
     * 테스트 엔드포인트
     * @return 성공 메시지
     */
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        return ResponseEntity.ok().body("성공");
    }

    /**
     * 로그인 API
     * @param request 이메일과 비밀번호를 포함한 로그인 요청 데이터
     * @return 로그인 결과를 포함한 응답
     */
    @PostMapping("/login")
    @Operation(summary = "로그인 API", description = "request 파라미터 : 이메일, 비밀번호(String)")
    public ResponseDTO<MemberResponseDTO.LoginDTO> login(@RequestBody MemberRequestDTO.LoginDTO request) {

        return ResponseDTO.onSuccess(memberService.login(request));

    }

    /**
     * 로그아웃 API
     * @param authentication
     * @param authorizationHeader
     * @return 로그아웃 결과 응답
     */
    @Operation(summary = "로그아웃 API", description = "로그아웃 API 입니다.")
    @PostMapping("/logout")
    public ResponseDTO<?> logout(Authentication authentication,HttpServletRequest authorizationHeader) {

        utilService.getAuthenticatedMember(authentication);
        memberService.logout(authorizationHeader);

        return ResponseDTO.of(SuccessStatus._OK,null);
    }

    /**
     * 카카오 계정 정보 조회 API
     * @param code 카카오 인증 코드
     * @return 카카오 계정 정보
     */
    @GetMapping("/callback/kakao")
    public ResponseDTO<?> getKakaoAccount(@RequestParam("code") String code) {

        return memberService.getKakaoInfo(code);

    }

    /**
     * 회원가입 API
     * @param request 회원가입 요청 데이터
     * @return 회원가입 결과를 포함한 응답
     */
    @PostMapping("/register")
    @Operation(summary = "회원가입 API", description = "이메일을 통해 회원가입하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4004", description = "이미 가입된 회원입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4006", description = "인증 번호가 일치하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
    })
    public ResponseDTO<MemberResponseDTO.JoinResultDTO> join(@RequestBody @Valid MemberRequestDTO.JoinDTO request) {

        return ResponseDTO.of(SuccessStatus._OK, MemberConverter.toJoinResultDTO(memberService.joinMember(request)));

    }

    @DeleteMapping("/delete")
    @Operation(summary = "회원 탈퇴 API", description = "회원 탈퇴 API입니다.")
    public ResponseDTO<?> deleteMember(Authentication authentication){

        Member member = utilService.getAuthenticatedMember(authentication);

        memberService.deleteMember(member);
        return ResponseDTO.onSuccess(SuccessStatus.MEMBER_DELETE_SUCCESS);
    }


    /**
     * 닉네임 중복 확인 API
     * @param request 닉네임 중복 확인 요청 데이터
     * @return 닉네임 중복 여부를 포함한 응답
     */
    @PostMapping("/nickname")
    @Operation(summary = "닉네임 중복 확인 API", description = "회원가입 시 회원이 입력한 닉네임의 중복 여부를 확인하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4008", description = "이미 존재하는 닉네임입니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class)))
    })
    public ResponseDTO<?> checkNickname(@RequestBody @Valid MemberRequestDTO.NicknameDTO request) {
        if (memberService.isDuplicate(request)) {
            throw new MemberHandler(ErrorStatus.MEMBER_NICKNAME_DUPLICATE);
        }

        return ResponseDTO.onSuccess("닉네임 중복이 아닙니다.");
    }


    @PutMapping("/nickname/update")
    @Operation(summary = "닉네임 변경 API", description = "토큰과 닉네임 넣어서 보내시면 됩니다.")
    public ResponseDTO<?> updateNickname(Authentication authentication,
                                         @RequestBody @Valid MemberRequestDTO.NicknameDTO request){
        if (memberService.isDuplicate(request)) {
            throw new MemberHandler(ErrorStatus.MEMBER_NICKNAME_DUPLICATE);
        }
        Member member = utilService.getAuthenticatedMember(authentication);

        memberService.updateNickname(member, request);

        return ResponseDTO.onSuccess("닉네임 변경 성공입니다.");
    }


    /**
     * 이메일 찾기 API
     * @param request 닉네임과 전화번호로 이메일을 찾기 위한 요청 데이터
     * @return 이메일 찾기 결과를 포함한 응답
     */
    @PostMapping("/email")
    @Operation(summary = "이메일 찾기 API", description = "닉네임과 전화번호로 이메일을 찾는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4006", description = "인증번호가 일치하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4001", description = "해당 회원을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
    })
    public ResponseDTO<MemberResponseDTO.EmailResultDTO> findEmail(@RequestBody @Valid MemberRequestDTO.FindEmailDTO request) {

        memberService.verifySms(request.getPhone(), request.getCertificationCode());

        Member member = memberQueryService.findEmail(request.getPhone());

        return ResponseDTO.of(SuccessStatus._OK, MemberConverter.toEmailResultDTO(member.getEmail()));

    }

    /**
     * 본인인증 문자 전송 API
     * @param request 본인인증을 위한 요청 데이터
     * @return 인증문자 전송 결과를 포함한 응답
     */
    @PostMapping("/send")
    @Operation(summary = "본인인증 문자 전송 API", description = "본인인증을 위한 인증번호 문자를 보내는 API입니다.")
    public ResponseDTO<?> sendSMS(@RequestBody MemberRequestDTO.SmsDTO request) {

        memberService.sendSms(request);

        return ResponseDTO.onSuccess("인증문자 전송 성공");

    }

    /**
     * 비밀번호 찾기 이메일 전송 API
     * @param request 비밀번호 찾기 요청 데이터
     * @return 이메일 전송 결과를 포함한 응답
     */
    @PostMapping("/password/send")
    @Operation(summary = "비빌번호 찾기 이메일 전송 API", description = "비밀번호를 찾기 위한 인증코드 이메일을 전송하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4001", description = "해당 회원을 찾을 수 없습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
    })
    public ResponseDTO<?> sendEmail(@RequestBody @Valid MemberRequestDTO.SendEmailDTO request) throws MessagingException, UnsupportedEncodingException {

        memberService.sendEmail(request);

        return ResponseDTO.of(SuccessStatus._OK, "메일 전송 성공");

    }

    /**
     * 비밀번호 찾기 API
     * @param request 비밀번호 찾기 요청 데이터
     * @return 인증 결과를 포함한 응답
     */
    @PostMapping("/password")
    @Operation(summary = "비밀번호 찾기 API", description = "비밀번호 찾기 페이지에서 인증코드가 맞는지 검사하는 API입니다. 해당 이메일을 응답으로 줍니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4006", description = "인증번호가 일치하지 않습니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class)))
    })
    public ResponseDTO<MemberResponseDTO.EmailResultDTO> findPassword(@RequestBody @Valid MemberRequestDTO.FindPassword request) {

        memberService.verifyEmail(request.getEmail(), request.getCertificationCode()); //인증 코드 검사

        return ResponseDTO.of(SuccessStatus._OK, MemberConverter.toEmailResultDTO(request.getEmail()));

    }


     /**
     * 비밀번호 재설정 API
     * 
     * 비밀번호 찾기 이후 재설정 페이지에서 새로운 비밀번호를 설정하는 API입니다.
     * 
     * @param request 비밀번호 재설정을 위한 요청 데이터
     * @return 비밀번호 재설정 결과를 포함한 응답
     */
    @PostMapping("/password/reset")
    @Operation(summary = "비밀번호 재설정 API", description = "비밀번호 찾기 이후 재설정 페이지에서 비밀번호를 재설정하는 API입니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "COMMON200", description = "성공입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "MEMBER4005", description = "재설정한 비밀번호가 서로 다릅니다.",
                    content = @Content(schema = @Schema(implementation = ErrorReasonDTO.class))),
    })
    public ResponseDTO<?> resetPassword(@RequestBody @Valid MemberRequestDTO.PasswordDTO request) {

        memberService.resetPassword(request);

        return ResponseDTO.of(SuccessStatus._OK, "비밀번호 재설정 성공");

    }

    /**
     * 앱 푸쉬 전송 API
     * 
     * FCM(Firebase Cloud Messaging)을 사용하여 앱 푸쉬 알림을 전송하는 API입니다.
     * 
     * @return 푸쉬 알림 전송 결과를 포함한 응답
     * @throws IOException 입출력 예외 발생 시
     */
    @PostMapping("/fcm/send")
    @Operation(summary = "앱 푸쉬 전송 api", description = "")
    public ResponseDTO<?> pushMessage() throws IOException {

        memberService.sendPushAlarm();

        return ResponseDTO.of(SuccessStatus.MEMBER_PUSH_SUCCESS, null);

    }

    /**
     * Family 초대 관련된 API를 처리하는 컨트롤러 클래스입니다.
     */
    private final FamilyService familyService;

    /**
     * 인증 코드를 이메일로 발송하는 API입니다.
     *
     * @param request 이메일 요청 DTO
     * @return 성공 메시지와 함께 응답을 반환합니다.
     */
    @PostMapping("/send-code")
    @Operation(summary = "인증 코드 발송 API", description = "이메일로 인증 코드를 발송하는 API입니다.")
    public ResponseDTO<?> sendVerificationCode(@RequestBody @Valid FamilyRequestDTO.EmailRequestDTO request) {

        familyService.sendVerificationCode(request.getEmail());
        return ResponseDTO.onSuccess("인증 코드 발송 성공");
    }

    /**
     * 이메일과 인증 코드를 검증하고 패밀리에 등록하는 API입니다.
     *
     * @param request 인증 요청 DTO
     * @return 성공 메시지와 함께 응답을 반환합니다.
     */
    @PostMapping("/verify")
    @Operation(summary = "인증 코드 검증 및 패밀리 등록 API", description = "이메일과 인증 코드를 검증하고 패밀리에 등록하는 API입니다.")
    public ResponseDTO<?> verifyAndRegisterFamily(@RequestBody @Valid FamilyRequestDTO.VerificationDTO request) {

        familyService.verifyAndRegisterFamily(request);
        return ResponseDTO.onSuccess("패밀리 등록 성공");
    }

    /**
     * 이메일과 닉네임 프로필 조회 API 입니다.
     *
     * @param authentication
     * @return
     */

    @GetMapping("/profile")
    @Operation(summary = "이메일, 닉네임 조회 API", description = "이메일과 닉네임을 조회하는 API입니다")
    public ResponseDTO<MemberResponseDTO.ProfileDTO> getEmailAndNickname(Authentication authentication){

        Member member = utilService.getAuthenticatedMember(authentication);

        return ResponseDTO.onSuccess(memberService.getEmailAndNickname(member));
    }


}
