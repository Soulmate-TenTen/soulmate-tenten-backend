package com.ten.soulmate.road.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ten.soulmate.global.dto.ResponseDto;
import com.ten.soulmate.road.dto.CheckCalendarRoadDto;
import com.ten.soulmate.road.dto.CheckCalendarRoadResponseDto;
import com.ten.soulmate.road.dto.GetRoadDto;
import com.ten.soulmate.road.dto.GetRoadResponseDto;
import com.ten.soulmate.road.service.RoadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequestMapping("/api/road")
@RestController
@RequiredArgsConstructor
@Tag(name = "Road API", description = "기로 관련 API")
public class RoadController {
	
	private final RoadService roadService;
		
	@Operation(summary = "기로 리스트 API", description = "선택한 날짜의 기로 리스트")
	@ApiResponses(value = {			
			@ApiResponse(responseCode = "200", description = "기로 리스트 조회 성공.",content = @Content(schema = @Schema(implementation = GetRoadResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "기로 리스트 조회 실패, 백엔드 개발자에게 로그 확인 요청.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
	@GetMapping("/getRoadList")
	public ResponseEntity<?> getRoadList(@ModelAttribute GetRoadDto request)
	{
		log.info("==================================[ getRoadList  ]==================================");	
		return roadService.getRoadList(request);
	}

	@Operation(summary = "기로 생성 날짜 리스트 API(켈린더 표시용)", description = "켈린더에 표시하기 위한 해당 월의 기로 날짜 리스트")
	@ApiResponses(value = {			
			@ApiResponse(responseCode = "200", description = "기로 생성 날짜 리스트 조회 성공.",content = @Content(schema = @Schema(implementation = CheckCalendarRoadResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "기로 생성 날짜 리스트 조회 실패, 백엔드 개발자에게 로그 확인 요청.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
	@GetMapping("/checkCalendarRoadDay")
	public ResponseEntity<?> CheckCalendarRoadDay(@ModelAttribute CheckCalendarRoadDto request)
	{
		log.info("==================================[ CheckCalendarRoadDay  ]==================================");	
		return roadService.CheckCalendarRoadDay(request);
	}
	
	
	
}
