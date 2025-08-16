package com.ten.soulmate.member.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ten.soulmate.global.dto.ResponseDto;
import com.ten.soulmate.member.dto.UpdateSoulmateNameDto;
import com.ten.soulmate.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/member")
@RestController
@RequiredArgsConstructor
@Tag(name = "Member API", description = "회원 관련 API")
public class MemberController {

	private final MemberService memberService;
	
	@Operation(summary = "소울메이트 이름변경", description = "소울메이트 이름변경")
	@ApiResponses(value = {			
			@ApiResponse(responseCode = "200", description = "소울메이트 이름변경 성공.",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "소울메이트 이름변경 실패, 백엔드 개발자에게 로그 확인 요청.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
	@PatchMapping("/updateSoulmateName")
	public ResponseEntity<?> updateSoulmateName(@RequestBody UpdateSoulmateNameDto request)
	{
		log.info("==================================[ updateSoulmateName  ]==================================");	
		return memberService.updateSoulmateName(request);
	}
	
	
	@Operation(summary = "회원 탈퇴", description = "회원 탈퇴")
	@ApiResponses(value = {			
			@ApiResponse(responseCode = "200", description = "회원 탈퇴 성공.",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "회원 탈퇴 실패, 백엔드 개발자에게 로그 확인 요청.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
	@DeleteMapping("/out")
	public ResponseEntity<?> deleteMember(@RequestParam("memberId") Long memberId){
		log.info("==================================[ deleteMember  ]==================================");	
		return memberService.deleteMember(memberId);
	}
	
	
	@Operation(summary = "오늘의 조언", description = "복권에 들어갈 조언")
	@ApiResponses(value = {			
			@ApiResponse(responseCode = "200", description = "오늘의 조언 생성 성공.",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "오늘의 조언 생성 실패, 백엔드 개발자에게 로그 확인 요청.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
	@GetMapping("/todayAdvice")
	public ResponseEntity<?> createTodayAdvice(@RequestParam("memberId") Long memberId){
		log.info("==================================[ createTodayAdvice  ]==================================");	
		return memberService.createTodayAdvice(memberId);
	}
	
	
	
}
