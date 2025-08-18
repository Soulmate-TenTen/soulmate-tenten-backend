package com.ten.soulmate.chatting.controller;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.ten.soulmate.chatting.dto.ChattingDto;
import com.ten.soulmate.chatting.dto.ChattingListResponseDto;
import com.ten.soulmate.chatting.dto.ResponseChattingDto;
import com.ten.soulmate.chatting.dto.ResponseReportDto;
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

	
	@Operation(summary = "채팅 메시지 전송", description = "사용자의 질문을 전송하고 AI의 답변을 SSE로 받기 위한 요청입니다.",
			requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
		            description = "채팅 요청 정보",
		            required = true,
		            content = @Content(schema = @Schema(implementation = ChattingDto.class))
		        ))
	@ApiResponses(value = {			
			@ApiResponse(responseCode = "200", description = "질문 전송 성공")
	})
    @PostMapping(value = "/sse/send", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter chatSSE(@RequestBody ChattingDto request)
    {
    	log.info("==================================[ SSE Send ]==================================");	
    	log.info("SSE Send Member Id : "+request.getMemberId());
    	
    	  SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

    	    try {
    	        // ChattingService에서 emitter를 관리하도록 위임
    	        chattingService.handleChatSSE(request, emitter);

    	    } catch (Exception e) {
    	        log.error("SSE 에러 발생", e);
    	        emitter.completeWithError(e);
    	    }

    	    return emitter;
    }
	
	
	@Operation(summary = "리포트 생성 요청", description = "채팅이 마무리된 후('REPORT' 도착) 리포트 생성 요청")
	@ApiResponses(value = {			
			@ApiResponse(responseCode = "200", description = "리포트 생성 성공.",content = @Content(schema = @Schema(implementation = ResponseReportDto.class))),
			@ApiResponse(responseCode = "400", description = "리포트 생성 실패, 백엔드 개발자에게 로그 확인 요청.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
    @PostMapping(value = "/createReport")
    public ResponseEntity<?> createReport(@RequestParam("memberId") Long memberId)
    {
    	log.info("==================================[ createReport ]==================================");	
    	log.info("Create Report Member Id : "+memberId);
    	
    	return chattingService.createReport(memberId);
    }
		
    
	@Operation(summary = "채팅 API", description = "채팅 발송 API")
	@ApiResponses(value = {			
			@ApiResponse(responseCode = "200", description = "채팅 발송 성공.",content = @Content(schema = @Schema(implementation = ResponseChattingDto.class))),
			@ApiResponse(responseCode = "400", description = "채팅 발송 실패, 백엔드 개발자에게 로그 확인 요청.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
    @PostMapping("/api/send")
    public ResponseEntity<?> chat(@RequestBody ChattingDto request)
    {
    	log.info("==================================[ API Chatting Send ]==================================");	
    	log.info("API Send Member Id : "+request.getMemberId());
    	
    	return chattingService.handleChat(request);
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
	
	
	@Operation(
		    summary = "채팅 중 중간에 나갔을 때(대화 내용 초기화)",
		    description = "채팅 중 리포트가 생성되지 않고 나간다면 대화가 초기화 됩니다."
		)
	@ApiResponses(value = {			
			@ApiResponse(responseCode = "200", description = "채팅 초기화 성공.",content = @Content(schema = @Schema(implementation = ResponseDto.class))),
			@ApiResponse(responseCode = "400", description = "채팅 초기화 실패, 백엔드 개발자에게 로그 확인 요청.",content = @Content(schema = @Schema(implementation = ResponseDto.class)))
	})
	@GetMapping("/reset")
	public ResponseEntity<?> chattingReset(@RequestParam("memberId") Long memberId)
	{
	    log.info("==================================[ Chatting Reset  ]==================================");
	    
	    return chattingService.chattingReset(memberId);
	}
	
}
