package com.ten.soulmate.chatting.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ten.soulmate.chatting.dto.ChattingDto;
import com.ten.soulmate.chatting.dto.ChattingListResponseDto;
import com.ten.soulmate.chatting.service.ChattingService;
import com.ten.soulmate.global.dto.ResponseDto;
import com.ten.soulmate.road.dto.CheckCalendarRoadResponseDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Slf4j
@RestController
@RequestMapping("/api/chatting")
@RequiredArgsConstructor
@Tag(name = "Chatting API", description = "채팅 관련 API")
public class ChattingController {
	
	private final ChattingService chattingService;

	@Operation( summary = "SSE 연결", description = "SSE 스트림을 통해 AI 챗봇의 실시간 응답을 수신합니다. 만약 응답으로 'REPORT'가 온다면 리포트 생성중입니다.\n리포트 생성이 완료되면 'roadId : 1' 이런 형식으로 roadId값을 드리겠습니다. 이 값은 기로 상세조회 API 사용 시 필요합니다.")
	@ApiResponses(value = {			
			@ApiResponse(responseCode = "200", description = "SSE 연결 성공, text/event-stream 형식으로 응답됨"
					,content =@Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE, schema = @Schema(type = "string")))
	})
    @GetMapping(value = "/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> connect(@RequestParam("memberId") Long memberId){    
		log.info("==================================[ SSE Connect  ]==================================");	
    	log.info("SSE Connection Member Id : "+memberId);
    	
		return chattingService.connect(memberId);
    }
    
	@Operation( summary = "채팅 메시지 전송", description = "사용자의 질문을 전송하고 AI의 답변을 SSE로 받기 위한 요청입니다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
		            description = "채팅 요청 정보",
		            required = true,
		            content = @Content(schema = @Schema(implementation = ChattingDto.class))
		        ))
	@ApiResponses(value = {			
			@ApiResponse(responseCode = "200", description = "질문 전송 성공")
	})
    @PostMapping("/sse/send")
    public void chat(@RequestBody ChattingDto request)
    {
    	log.info("==================================[ SSE Send  ]==================================");	
    	log.info("SSE Send Member Id : "+request.getMemberId());
    	
    	chattingService.handleChat(request);
    }
	
	@Operation(
	    summary = "SSE 연결 종료",
	    description = "클라이언트 로그아웃 시 SSE 연결을 종료합니다."
	)
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "SSE 연결 종료 성공")
	})
	@PostMapping("/sse/close")
	public void closeSse(@RequestParam("memberId") Long memberId) {
	    log.info("==================================[ SSE Close  ]==================================");
	    log.info("SSE Close Member Id : " + memberId);

	    chattingService.disconnect(memberId);
	}
    
	
	@Operation(summary = "대화 내용 조회 API", description = "대화 내용 조회")
	@ApiResponses(value = {			
			@ApiResponse(responseCode = "200", description = "대화 내용 조회 성공.",content = @Content(schema = @Schema(implementation = ChattingListResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "대화 내용 조회 실패, 백엔드 개발자에게 로그 확인 요청.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
	@GetMapping("/chattingList")
	public ResponseEntity<?> getChattingList(@RequestParam("roadId") Long roadId)
	{
		log.info("==================================[ Get ChattingList  ]==================================");
	
		return chattingService.getChattingList(roadId);
	}
}
