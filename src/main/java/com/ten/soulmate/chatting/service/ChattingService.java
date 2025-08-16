package com.ten.soulmate.chatting.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ten.soulmate.chatting.dto.AiRequestDto;
import com.ten.soulmate.chatting.dto.ChattingDto;
import com.ten.soulmate.chatting.dto.ChattingListDto;
import com.ten.soulmate.chatting.dto.ChattingListResponseDto;
import com.ten.soulmate.chatting.dto.GetChattingListDto;
import com.ten.soulmate.chatting.dto.ReportAiResponse;
import com.ten.soulmate.chatting.dto.ResponseChattingDto;
import com.ten.soulmate.chatting.dto.SummaryAiResponse;
import com.ten.soulmate.chatting.entity.Chatting;
import com.ten.soulmate.chatting.entity.ChattingList;
import com.ten.soulmate.chatting.repository.ChattingListRepository;
import com.ten.soulmate.chatting.repository.ChattingRepository;
import com.ten.soulmate.global.dto.ResponseDto;
import com.ten.soulmate.global.type.AnswerType;
import com.ten.soulmate.global.type.ChatType;
import com.ten.soulmate.global.type.SoulMateType;
import com.ten.soulmate.member.entity.Member;
import com.ten.soulmate.member.entity.MemberAttribute;
import com.ten.soulmate.member.repository.MemberAttributeRepository;
import com.ten.soulmate.member.repository.MemberRepository;
import com.ten.soulmate.road.entity.Road;
import com.ten.soulmate.road.repository.RoadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChattingService {

	//사용자 ID + SSE 스트림 맵
	private final Map<Long, Sinks.Many<String>> userSinkMap = new ConcurrentHashMap<Long, Sinks.Many<String>>();

	//임시 저장용 Map
    private final Map<Long, List<ChattingListDto>> tempChatMap = new ConcurrentHashMap<>();
    
    private final ChattingRepository chattingRepository;
    private final ChattingListRepository chattingListRepository;
    private final MemberRepository memberRepository;
    private final MemberAttributeRepository memberAttributeRepository;
    private final RoadRepository roadRepository;
    private final AiChatService aiChatService;
	
   
    public void putCompletedMessage(Long memberId, String message) {
        Sinks.Many<String> sink = userSinkMap.get(memberId);
        if (sink != null) {
            sink.tryEmitNext(message);
        } else {
            log.warn("Sink not found for memberId: {}", memberId);
        }
    }
    
	//SSE 연결 요청
    private Flux<String> connect(Long memberId) {
        // 이미 연결된 sink가 있는 경우, 해당 Flux 그대로 반환
        if (userSinkMap.containsKey(memberId)) {
            log.warn("SSE Connection Already Exists! [memberId: {}]", memberId);
            return userSinkMap.get(memberId).asFlux();
        }

        // 새 연결 생성
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        userSinkMap.put(memberId, sink);
        tempChatMap.put(memberId, new ArrayList<>());
        log.info("SSE Connection Success! [memberId : {}]", memberId);

        return sink.asFlux()
                .doFinally(signalType -> {
                    userSinkMap.remove(memberId);
                    tempChatMap.remove(memberId);
                    log.info("SSE Disconnected! [memberId : {}]", memberId);
                });
    }
    
    
    @Transactional
    public ResponseEntity<?> handleChat(ChattingDto request) {
    	
    	ResponseChattingDto responseChattingDto = null;
    	
    	try {
    		 Long memberId = request.getMemberId();
    	        String message = request.getQuestion();

    	        if(tempChatMap.get(memberId) == null) {
    	        	tempChatMap.put(memberId, new ArrayList<>());
    	        }                
    	        
    	        // 1. 메시지 임시 저장
    	        ChattingListDto chattingListDto = ChattingListDto.builder()
    	        									.message(message)
    	        									.createAt(LocalDateTime.now())
    	        									.answerType(AnswerType.N)
    	        									.chatType(ChatType.M)
    	        									.build();
    	        	        	        
    	        tempChatMap.get(memberId).add(chattingListDto);
    	        
    	        //AI에게 요청할 데이터를 세팅
    	        Member member = memberRepository.findById(memberId).get();
    	        MemberAttribute memberAttribute = memberAttributeRepository.findByMemberId(memberId).get();
    	        String memberName = member.getName();
    	        String soulmateName = member.getSoulmateName();
    	        String valueAttribute = memberAttribute.getValueAttribute();
    	        String decision = memberAttribute.getDecision();
    	        String regret = memberAttribute.getRegret();
    	        String decisionTrust = memberAttribute.getDecisionTrust();
    	        SoulMateType soulmateType = member.getSoulmateType();    	        

    	        AiRequestDto aiRequestDto = AiRequestDto.builder()
    	        							.message(message)
    	        							.memberName(memberName)
    	        							.soulmateName(soulmateName)
    	        							.valueAttribute(valueAttribute)
    	        							.decision(decision)
    	        							.regret(regret)
    	        							.decisionTrust(decisionTrust)
    	        							.soulMateType(soulmateType)
    	        							.build();
    	                			
    	        
    	        //정보량 판단로직에 그동안 수행한 데이터를 전달해야 함
    	        aiRequestDto.setMessage(buildUserPrompt(tempChatMap.get(memberId), "HCX-005"));           	           	         
    	        
    	        //2. 정보량 판단 로직
    	        //true -> 리포트 생성 가능
    	        //flase -> 대화 이어서 하기
    	        if (aiChatService.ResponseCheckMessage(aiRequestDto)) {
    	        	
    	        	log.info("REPORT 생성 가능 [memberId: {}]", memberId);

    	            // REPORT 처리
    	            String userPromptReport = buildUserPrompt(tempChatMap.get(memberId), "HCX-007");
    	            aiRequestDto.setMessage(userPromptReport);

    	            //리포트 생성 AI 호출
    	            ReportAiResponse reportData = aiChatService.ResponseReportMessage(aiRequestDto);

    	            ChattingListDto reportDto = ChattingListDto.builder()
    	                    .message("REPORT")
    	                    .createAt(LocalDateTime.now())
    	                    .answerType(AnswerType.R)
    	                    .chatType(ChatType.A)
    	                    .build();

    	            tempChatMap.get(memberId).add(reportDto);

    	            String summaryPrompt = buildUserPrompt(tempChatMap.get(memberId), "Summary");
    	            aiRequestDto.setMessage(summaryPrompt);

    	            //요약 AI 호출
    	            SummaryAiResponse summary = aiChatService.ResponseSummaryMessage(aiRequestDto);
    	            Long roadId = saveToDB(memberId, tempChatMap.get(memberId), summary, reportData);
    	            
    	            responseChattingDto = ResponseChattingDto.builder()
    	            						.answerType(AnswerType.R)
    	            						.message("REPORT")
    	            						.roadId(roadId).build();
    	            
    	            log.info("[member Id :"+memberId+"] Road Id : "+roadId);    	            
    	            
    	            return ResponseEntity.ok(responseChattingDto);
    	        }
    	        else {
    	        	
    	        	log.info("리포트 생성 정보 부족 - 대화 계속 진행");
    	        	aiRequestDto.setMessage(buildUserPrompt(tempChatMap.get(memberId), "DASH"));  
    	        	String aiResponse = aiChatService.ResponseChatMessage(aiRequestDto);
    	        	
    	        	ChattingListDto responseAiDto = ChattingListDto.builder()
    	                    .message(aiResponse)
    	                    .createAt(LocalDateTime.now())
    	                    .answerType(AnswerType.N)
    	                    .chatType(ChatType.A)
    	                    .build();
    	        	
    	        	//AI응답 담기
    	        	tempChatMap.get(memberId).add(responseAiDto);
    	        	
    	        	responseChattingDto = ResponseChattingDto.builder()
    						.answerType(AnswerType.N)
    						.message(aiResponse).build();
    	        	
    	        	return ResponseEntity.ok(responseChattingDto);
    	        }
    		   	   		
    	} catch(Exception e)
    	{
    		log.error("Chatting Error : "+e.getMessage());
    		ResponseDto res = new ResponseDto();
    		res.setMessage("Failed");
    		
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    	}

    }   
	
    //채팅 로직        
//    @Transactional
//    public Flux<String> handleChatSSE(ChattingDto request) {
//        Long memberId = request.getMemberId();
//        String message = request.getQuestion();
//
//        connect(memberId);
//
//        Sinks.Many<String> sink = userSinkMap.get(memberId);
//        if (sink == null) {
//            return Flux.empty();
//        }
//
//        ChattingListDto chattingListDto = ChattingListDto.builder()
//                .message(message)
//                .createAt(LocalDateTime.now())
//                .answerType(AnswerType.N)
//                .chatType(ChatType.M)
//                .build();
//
//        tempChatMap.get(memberId).add(chattingListDto);
//        log.info("SSE Send Success! [memberId : {}]", memberId);
//
//        Member member = memberRepository.findById(memberId).get();
//        MemberAttribute memberAttribute = memberAttributeRepository.findByMemberId(memberId).get();
//
//        AiRequestDto aiRequestDto = AiRequestDto.builder()
//                .message(buildUserPrompt(tempChatMap.get(memberId), "DASH"))
//                .memberName(member.getName())
//                .soulmateName(member.getSoulmateName())
//                .valueAttribute(memberAttribute.getValueAttribute())
//                .decision(memberAttribute.getDecision())
//                .regret(memberAttribute.getRegret())
//                .decisionTrust(memberAttribute.getDecisionTrust())
//                .soulMateType(member.getSoulmateType())
//                .build();
//
//        Flux<String> tokenStream = aiChatService.ResponseChatMessageSSE(aiRequestDto, sink)
//                .doOnNext(token -> {
//                    log.debug("Token: {}", token);
//                });
//
//        Flux<String> completeMessageFlux = tokenStream
//                .filter(this::hasUsage)
//                .flatMap(rawEvent -> Mono.justOrEmpty(extractMessageContent(rawEvent)))
//                .flatMap(completedMessage -> {
//                    ChattingListDto aiResponseDto = ChattingListDto.builder()
//                            .chatting(null)
//                            .member(null)
//                            .message(completedMessage)
//                            .createAt(LocalDateTime.now())
//                            .answerType(AnswerType.N)
//                            .chatType(ChatType.A)
//                            .build();
//
//                    tempChatMap.get(memberId).add(aiResponseDto);
//                    log.info("완성된 AI 메시지 저장 완료 [memberId: {}]", memberId);
//
//                    String userPrompt = buildUserPrompt(tempChatMap.get(memberId), "HCX-005");
//                    aiRequestDto.setMessage(userPrompt);
//
//                    if (aiChatService.ResponseCheckMessage(aiRequestDto)) {
//                        log.info("REPORT 시작 [memberId: {}]", memberId);
//
//                        // REPORT 이벤트만 먼저 보내는 Flux
//                        Flux<String> reportFlux = Flux.just("REPORT")
//                                .doOnNext(event -> log.info("REPORT 이벤트 전송: {}", event));
//
//                        // REPORT 처리
//                        String userPromptReport = buildUserPrompt(tempChatMap.get(memberId), "HCX-007");
//                        aiRequestDto.setMessage(userPromptReport);
//
//                        ReportAiResponse reportData = aiChatService.ResponseReportMessage(aiRequestDto);
//
//                        ChattingListDto reportDto = ChattingListDto.builder()
//                                .message("REPORT")
//                                .createAt(LocalDateTime.now())
//                                .answerType(AnswerType.R)
//                                .chatType(ChatType.A)
//                                .build();
//
//                        tempChatMap.get(memberId).add(reportDto);
//
//                        String summaryPrompt = buildUserPrompt(tempChatMap.get(memberId), "Summary");
//                        aiRequestDto.setMessage(summaryPrompt);
//
//                        SummaryAiResponse summary = aiChatService.ResponseSummaryMessage(aiRequestDto);
//
//                        Long roadId = saveToDB(memberId, tempChatMap.get(memberId), summary, reportData);
//
//                        // roadId 이벤트 Flux
//                        Flux<String> roadIdFlux = Flux.just("roadId : " + roadId)
//                                .doOnNext(event -> log.info("roadId 이벤트 전송: {}", event))
//                                .concatWith(Mono.fromRunnable(() -> {
//                                    tempChatMap.put(memberId, new ArrayList<>());
//                                    disconnect(memberId);
//                                }));
//
//                        // REPORT 이벤트 전송 후 roadId 이벤트 전송
//                        return roadIdFlux;
//
//                    } else {
//                        return Flux.empty();
//                    }
//                });
//
//        return Flux.merge(tokenStream, completeMessageFlux)
//                .doOnComplete(() -> {
//                    log.info("AI 토큰 스트림 완료 [memberId: {}]", memberId);
//                    // disconnect는 roadId 이벤트에서 처리함
//                });
//    }


    //사전로직 검사 코드 - 고도화때 진행
    @Transactional
    public Flux<String> handleChatSSE(ChattingDto request) {
        Long memberId = request.getMemberId();
        String message = request.getQuestion();

        connect(memberId);

        Sinks.Many<String> sink = userSinkMap.get(memberId);
        if (sink == null) {
            return Flux.empty();
        }

        // 사용자 메시지 저장
        ChattingListDto chattingListDto = ChattingListDto.builder()
                .message(message)
                .createAt(LocalDateTime.now())
                .answerType(AnswerType.N)
                .chatType(ChatType.M)
                .build();
        tempChatMap.get(memberId).add(chattingListDto);

        log.info("SSE Send Success! [memberId : {}]", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 정보가 없습니다. memberId=" + memberId));
        MemberAttribute memberAttribute = memberAttributeRepository.findByMemberId(memberId)
                .orElseThrow(() -> new RuntimeException("회원 속성 정보가 없습니다. memberId=" + memberId));

        AiRequestDto aiRequestDto = AiRequestDto.builder()
                .message(buildUserPrompt(tempChatMap.get(memberId), "HCX-005"))
                .memberName(member.getName())
                .soulmateName(member.getSoulmateName())
                .valueAttribute(memberAttribute.getValueAttribute())
                .decision(memberAttribute.getDecision())
                .regret(memberAttribute.getRegret())
                .decisionTrust(memberAttribute.getDecisionTrust())
                .soulMateType(member.getSoulmateType())
                .build();

//        if (aiChatService.ResponseCheckMessage(aiRequestDto)) {
//            log.info("REPORT 시작 [memberId: {}]", memberId);
//
//            // 1️⃣ REPORT 즉시 발송
//            Flux<String> reportFlux = Flux.just("REPORT")
//                    .doOnNext(event -> {
//                        log.info("REPORT 이벤트 전송: {}", event);
//                        ChattingListDto reportDto = ChattingListDto.builder()
//                                .message("REPORT")
//                                .createAt(LocalDateTime.now())
//                                .answerType(AnswerType.R)
//                                .chatType(ChatType.A)
//                                .build();
//                        tempChatMap.get(memberId).add(reportDto);
//                    });
//
//            // 2️⃣ 무거운 작업은 백그라운드 실행
//            Mono<Long> roadIdMono = Mono.fromCallable(() -> {
//                        String userPromptReport = buildUserPrompt(tempChatMap.get(memberId), "HCX-007");
//                        aiRequestDto.setMessage(userPromptReport);
//                        ReportAiResponse reportData = aiChatService.ResponseReportMessage(aiRequestDto);
//
//                        String summaryPrompt = buildUserPrompt(tempChatMap.get(memberId), "Summary");
//                        aiRequestDto.setMessage(summaryPrompt);
//                        SummaryAiResponse summary = aiChatService.ResponseSummaryMessage(aiRequestDto);
//
//                        return saveToDB(memberId, tempChatMap.get(memberId), summary, reportData);
//                    })
//                    .subscribeOn(Schedulers.boundedElastic()) // 이벤트 루프 블로킹 방지
//                    .doOnSuccess(roadId -> log.info("roadId 생성 완료: {}", roadId));
//
//            // 3️⃣ roadId 발송 + 정리
//            Flux<String> roadIdFlux = roadIdMono
//                    .map(roadId -> "roadId : " + roadId)
//                    .concatWith(Mono.fromRunnable(() -> {
//                        tempChatMap.put(memberId, new ArrayList<>());
//                        disconnect(memberId);
//                    }));
//
//            // REPORT → roadId 순서로 발행
//            return reportFlux.concatWith(roadIdFlux);
//        }
        
        
        if (aiChatService.ResponseCheckMessage(aiRequestDto)) {
            log.info("REPORT 시작 [memberId: {}]", memberId);

            // 1️⃣ REPORT 즉시 발송
            Flux<String> reportFlux = Flux.just("REPORT")
                    .doOnNext(event -> {
                        log.info("REPORT 이벤트 전송: {}", event);
                        ChattingListDto reportDto = ChattingListDto.builder()
                                .message("REPORT")
                                .createAt(LocalDateTime.now())
                                .answerType(AnswerType.R)
                                .chatType(ChatType.A)
                                .build();
                        tempChatMap.get(memberId).add(reportDto);
                    })
                    .publishOn(Schedulers.boundedElastic()); // 브라우저에 즉시 flush 가능

            // 2️⃣ 무거운 작업은 백그라운드 실행
            Mono<String> roadIdMono = Mono.fromCallable(() -> {
                        String userPromptReport = buildUserPrompt(tempChatMap.get(memberId), "HCX-007");
                        aiRequestDto.setMessage(userPromptReport);
                        ReportAiResponse reportData = aiChatService.ResponseReportMessage(aiRequestDto);

                        String summaryPrompt = buildUserPrompt(tempChatMap.get(memberId), "Summary");
                        aiRequestDto.setMessage(summaryPrompt);
                        SummaryAiResponse summary = aiChatService.ResponseSummaryMessage(aiRequestDto);

                        Long roadId = saveToDB(memberId, tempChatMap.get(memberId), summary, reportData);
                        return "roadId : " + roadId;
                    })
                    .subscribeOn(Schedulers.boundedElastic())
                    .doOnSuccess(event -> {
                        log.info("roadId 생성 완료: {}", event);
                        tempChatMap.put(memberId, new ArrayList<>());
                        disconnect(memberId);
                    });

            // REPORT → roadId 순서로 발행
            return reportFlux.concatWith(roadIdMono.flux());
        }
        
        
        // ❌ 조건이 false일 경우: AI 응답 완성 후 저장
        aiRequestDto.setMessage(buildUserPrompt(tempChatMap.get(memberId), "DASH"));

        Flux<String> tokenStream = aiChatService.ResponseChatMessageSSE(aiRequestDto, sink);

        // 완성된 문장만 추출해서 Map에 저장 (클라이언트로 보내지 않음)
        tokenStream
            .filter(this::hasUsage)
            .flatMap(rawEvent -> Mono.justOrEmpty(extractMessageContent(rawEvent)))
            .doOnNext(completedMessage -> {
                ChattingListDto aiResponseDto = ChattingListDto.builder()
                        .message(completedMessage)
                        .createAt(LocalDateTime.now())
                        .answerType(AnswerType.N)
                        .chatType(ChatType.A)
                        .build();
                tempChatMap.get(memberId).add(aiResponseDto);
                log.info("AI 응답 저장 완료 [memberId: {}]", memberId);
            })
            .subscribe(); // 📌 토큰 스트림과 별도로 저장 로직만 실행

        // 리턴은 AI 토큰 스트림 그대로
        return tokenStream;
    }
    
    
    
    
	@Transactional
	public Long saveToDB(Long memberId, List<ChattingListDto> chatList, SummaryAiResponse summary, ReportAiResponse reportData) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다."));

        Chatting chatting = chattingRepository.findActiveChatting(memberId)
        		.orElseThrow(() -> new RuntimeException("채팅방을 찾을 수 없습니다."));

        List<ChattingList> entities = chatList.stream()
            .map(dto -> ChattingList.builder()
                .chatting(chatting)
                .member(member)
                .message(dto.getMessage())
                .answerType(dto.getAnswerType())
                .chatType(dto.getChatType())
                .build())
            .collect(Collectors.toList());

        chattingListRepository.saveAll(entities);
                
        //채팅이 종료되었기 때문에 Chatting의 finYn Y로 변경
	    chattingRepository.updateFinYnToYByChatId(chatting.getId());
	    
	    //다음 채팅방을 생성
	    Chatting newChatting = Chatting.builder()
	    						.member(member)
	    						.finYn("N")
	    						.build();
	    chattingRepository.save(newChatting);
	    
	    log.info("New Chatting Room Created!");	        
	    	    
	    //기로 생성
	    Road road = Road.builder()
	    			.member(member)
	    			.chatting(chatting)
	    			.title(summary.getSummaryTitle())
	    			.summary(summary.getSummaryContent())
	    			.titleA(reportData.getTitleA())
	    			.answerA(reportData.getAnswerA())
	    			.titleB(reportData.getTitleB())
	    			.answerB(reportData.getAnswerB())
	    			.recommend(reportData.getRecommend())
	    			.titleConclusion(reportData.getTitleConclusion())
	    			.conclusion(reportData.getConclusion())
	    			.thinkinContent(reportData.getThinkingContent())
	    			.category(reportData.getCategory())
	    			.build();
	    
	   return roadRepository.saveAndFlush(road).getId();    	    
    }
	
	private String buildUserPrompt(List<ChattingListDto> chattingList, String type) {
	    StringBuilder userPrompt = new StringBuilder();
	    
	    userPrompt.append("--- 대화 시작 ---\n");

	    for (ChattingListDto chat : chattingList) {
	        if (chat.getChatType() == ChatType.M) {
	            userPrompt.append("User : ").append(chat.getMessage()).append("\n");
	        } else if (chat.getChatType() == ChatType.A) {
	            userPrompt.append("Assistant : ").append(chat.getMessage()).append("\n");
	        }
	    }

	    userPrompt.append("--- 대화 끝 ---\n");
	    
	    if(type.equals("HCX-005"))
	    {
	    	userPrompt.append("이 대화를 바탕으로 보고서를 작성하기에 필요한 정보가 충분한가요?\n");
		    userPrompt.append("[답변은 answer로만 해주세요]\n");
	    }	
	    
	    if(type.equals("HCX-007"))
	    {
	    	userPrompt.append("### 요청 ###\r\n"
	    			+ "- 소울메이트(assistant)의 말은 제외하고, 오로지 사용자(user)의 말들 중에서 판단할 수 있는 부분을 찾아야 합니다.\r\n"
	    			+ "- 근거는 납득 가능해야 합니다.\r\n"
	    			+ "- 부차적인 말은 생략하고, 판단 근거에 집중해주세요.");
	    }	
	   	    
	    if(type.equals("Summary"))
	    {
	    	userPrompt.append("이 대화를 바탕으로 요약된 제목(title)과 내용(content)을 알려주세요.");
	    }
	    
	    if(type.equals("DASH"))
	    {
	    	userPrompt.append("대화 내용을 바탕으로 친구(user)의 최근 질문에 답해주세요.");
	    }
	    
	    return userPrompt.toString();
	}
	
	private void disconnect(Long memberId) {
	    if (userSinkMap.containsKey(memberId)) {
	        userSinkMap.get(memberId).tryEmitComplete(); // 스트림 종료
	        userSinkMap.remove(memberId);                // Sink 제거
	        tempChatMap.remove(memberId);                // 채팅 내용 초기화

	        log.info("SSE Disconnected. [memberId : {}]", memberId);
	    } else {
	        log.warn("No SSE Connection found to close for memberId : {}", memberId);
	    }
	}
	
	
	public ResponseEntity<?> getChattingList(Long roadId)
	{
		try {
			
			Optional<Road> road = roadRepository.findById(roadId);
			Long chatId = road.get().getChatting().getId();
			
			List<ChattingList> getChattingList = chattingListRepository.findByChattingId(chatId);
			List<GetChattingListDto> chattingList = new ArrayList<GetChattingListDto>();
			
			for(ChattingList chatting : getChattingList)
			{
				GetChattingListDto chat = GetChattingListDto.builder()
													.chatType(chatting.getChatType())
													.message(chatting.getMessage())
													.createAt(chatting.getCreateAt())
													.build();					
				chattingList.add(chat);
			}
			
			ChattingListResponseDto chattingDataList = new ChattingListResponseDto();
			chattingDataList.setChattingList(chattingList);
			
			log.info("Get Chatting List Success!");
			
			return ResponseEntity.ok(chattingDataList);			
		} catch(Exception e)
		{;
			ResponseDto res = new ResponseDto();
			res.setMessage("Failed");
			log.error("Get Chatting List Error : "+e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
		}
	}
	
	public ResponseEntity<?> chattingReset(Long memberId)
	{		
		ResponseDto res = new ResponseDto();
		try {		
            tempChatMap.put(memberId, new ArrayList<>());
            //disconnect(memberId);           
            
			log.info("Chatting Reset Success!");

			res.setMessage("Success");
			return ResponseEntity.ok(res);
		} catch(Exception e)
		{
			res.setMessage("Failed");
			log.error("Chatting Reset Error : "+e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
		}
	}
	
	private String extractMessageContent(String rawEvent) {
        try {
            int jsonStart = rawEvent.indexOf('{');
            if (jsonStart == -1) return null;

            String jsonPart = rawEvent.substring(jsonStart);
            JsonNode root = new ObjectMapper().readTree(jsonPart);
            JsonNode contentNode = root.path("message").path("content");

            if (!contentNode.isMissingNode() && !contentNode.asText().isBlank()) {
                return contentNode.asText();
            }
        } catch (Exception e) {
            log.error("JSON 파싱 오류", e);
        }
        return null;
    }

    private boolean hasUsage(String rawEvent) {
        return rawEvent.contains("\"usage\"") && !rawEvent.contains("\"usage\":null");
    }
	
}
