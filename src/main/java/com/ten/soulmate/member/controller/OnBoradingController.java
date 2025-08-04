package com.ten.soulmate.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ten.soulmate.member.dto.OnBoardingDto;
import com.ten.soulmate.member.service.OnBoardingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequestMapping("/api/onboarding")
@RestController
@RequiredArgsConstructor
@Tag(name = "Onboarding API", description = "온보딩 관련 API")
public class OnBoradingController {

	private final OnBoardingService onBoardingService;
	
	@Operation(summary = "온보딩 결과 삽입 API", description = "온보딩 질문 4개의 대답과 소울메이트의 성향을 저장합니다.")
	@PostMapping("/onboardingResult")
	public ResponseEntity<?> setOnBoardingResult(@RequestBody OnBoardingDto request)
	{		
		log.info("==================================[ Set OnBoardingResult  ]==================================");
		return onBoardingService.setOnBoardingResult(request);
	}
	
	
}
