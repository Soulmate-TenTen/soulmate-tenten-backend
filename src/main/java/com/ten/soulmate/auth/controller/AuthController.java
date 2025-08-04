package com.ten.soulmate.auth.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ten.soulmate.auth.dto.LoginDto;
import com.ten.soulmate.auth.dto.LoginResponseDto;
import com.ten.soulmate.auth.service.AuthService;
import com.ten.soulmate.global.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/auth")
@RestController
@RequiredArgsConstructor
@Tag(name = "Auth API", description = "인증 관련 API")
public class AuthController {

	private final AuthService authService;
	
	@Operation(summary = "로그인 API", description = "카카오 회원으로 부터 받은 회원 정보를 저장합니다. email, name")
	@ApiResponses(value = {			
			@ApiResponse(responseCode = "200", description = "회원 인증 성공.",content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
			@ApiResponse(responseCode = "401", description = "회원 인증 실패, 백엔드 개발자에게 로그 확인 요청.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginDto request) {
		log.info("==================================[ Login  ]==================================");	
		
		return authService.login(request);
	}
	
}
