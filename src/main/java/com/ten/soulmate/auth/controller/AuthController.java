package com.ten.soulmate.auth.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ten.soulmate.auth.dto.LoginDto;
import com.ten.soulmate.auth.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
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
	
	@Operation(summary = "로그인 API", description = "카카오 회원으로 부터 받은 회원 정보를 저장합니다. email, name, profileImg")
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginDto request) {
		log.info("==================================[ Login  ]==================================");	
		
		return authService.login(request);
	}
	
}
