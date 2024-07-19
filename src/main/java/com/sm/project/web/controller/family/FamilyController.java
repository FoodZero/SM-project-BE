package com.sm.project.web.controller.family;

import com.sm.project.apiPayload.ResponseDTO;
import com.sm.project.service.family.FamilyService;
import com.sm.project.web.dto.family.FamilyRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "Family", description = "Family 관련 API")
@RequestMapping("/api/family")
public class FamilyController {

    private final FamilyService familyService;

    @PostMapping("/send-code")
    @Operation(summary = "인증 코드 발송 API", description = "이메일로 인증 코드를 발송하는 API입니다.")
    public ResponseDTO<?> sendVerificationCode(@RequestBody @Valid FamilyRequestDTO.EmailRequestDTO request) {
        familyService.sendVerificationCode(request.getEmail());
        return ResponseDTO.onSuccess("인증 코드 발송 성공");
    }

    @PostMapping("/verify")
    @Operation(summary = "인증 코드 검증 및 패밀리 등록 API", description = "이메일과 인증 코드를 검증하고 패밀리에 등록하는 API입니다.")
    public ResponseDTO<?> verifyAndRegisterFamily(@RequestBody @Valid FamilyRequestDTO.VerificationDTO request) {
        familyService.verifyAndRegisterFamily(request);
        return ResponseDTO.onSuccess("패밀리 등록 성공");
    }
}
