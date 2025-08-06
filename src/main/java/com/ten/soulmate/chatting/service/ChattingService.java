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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.ten.soulmate.chatting.dto.AiRequestDto;
import com.ten.soulmate.chatting.dto.ChattingDto;
import com.ten.soulmate.chatting.dto.ChattingListDto;
import com.ten.soulmate.chatting.dto.ChattingListResponseDto;
import com.ten.soulmate.chatting.dto.GetChattingListDto;
import com.ten.soulmate.chatting.dto.ReportAiResponse;
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
import reactor.core.publisher.Sinks;

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
	
	//SSE 연결 요청
	public Flux<String> connect(Long memberId){    	
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
		userSinkMap.put(memberId, sink);
        tempChatMap.put(memberId, new ArrayList<>());
        log.info("SSE Connection Success! [memberId : "+memberId+"]");
		
        return sink.asFlux()
                .doFinally(signalType -> {
                    userSinkMap.remove(memberId);
                    tempChatMap.remove(memberId); 
                });		
    }
	
	//채팅 로직
	@Transactional
	public void handleChat(ChattingDto request) {
        Long memberId = request.getMemberId();
        String message = request.getQuestion();

        Sinks.Many<String> sink = userSinkMap.get(memberId);
        if (sink == null) return;

        // 1. 메시지 임시 저장
        ChattingListDto chattingListDto = ChattingListDto.builder()
        									.message(message)
        									.createAt(LocalDateTime.now())
        									.answerType(AnswerType.N)
        									.chatType(ChatType.M)
        									.build();
        	        	        
        tempChatMap.get(memberId).add(chattingListDto);
        
        log.info("SSE Send Success! [memberId : "+memberId+"]");
        
        //여기에 챗봇 로직 추가해야함
        Member member = memberRepository.findById(memberId).get();
        MemberAttribute memberAttribute = memberAttributeRepository.findByMemberId(memberId).get();
        String memberName = member.getName();
        String soulmateName = member.getSoulmateName();
        String valueAttribute = memberAttribute.getValueAttribute();
        String decision = memberAttribute.getDecision();
        String regret = memberAttribute.getRegret();
        String decisionTrust = memberAttribute.getDecisionTrust();
        SoulMateType soulmateType = member.getSoulmateType();
        
        //1. 대화형 챗봇 로직
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
        							       
        
        String userPromptChat = buildUserPrompt(tempChatMap.get(memberId), "DASH");             
        aiRequestDto.setMessage(userPromptChat);
        String aiResponse = aiChatService.ResponseChatMessage(aiRequestDto);
        
        log.info("AI CHAT : "+aiResponse);
        
        ChattingListDto aiResponseDto = ChattingListDto.builder()
                .chatting(null)
                .member(null)
                .message(aiResponse)
                .createAt(LocalDateTime.now())
                .answerType(AnswerType.N)
                .chatType(ChatType.A)
                .build(); 
                       
        //2. 정보량 판단 로직       
        //여기에 정보량 판단 로직 추가하고 판단 결과를 담아야함      
        String userPrompt = buildUserPrompt(tempChatMap.get(memberId), "HCX-005");             
        aiRequestDto.setMessage(userPrompt);
        AnswerType AiAnswerType = null; 
        ReportAiResponse reportData = null;
        
        if(aiChatService.ResponseCheckMessage(aiRequestDto))
        {
        	AiAnswerType = AnswerType.R;        
        	        	
        	//리포트 생성가능 알림(프론트에 전달)
        	sink.tryEmitNext("REPORT");       	
        	       	
        	//리포트 생성로직 필요
        	String userPromptReport = buildUserPrompt(tempChatMap.get(memberId), "HCX-007"); 
        	aiRequestDto.setMessage(userPromptReport);
        	reportData = aiChatService.ResponseReportMessage(aiRequestDto);       	        
        	
        	ChattingListDto aiResponseDtoReport = ChattingListDto.builder()
                    .chatting(null)
                    .member(null)
                    .message("REPORT")
                    .createAt(LocalDateTime.now())
                    .answerType(AnswerType.R)
                    .chatType(ChatType.A)
                    .build();       	
        	        	
        	
        	//리포트 내역 넣기
        	tempChatMap.get(memberId).add(aiResponseDtoReport);
        }
        else {
        	AiAnswerType = AnswerType.N;
        	tempChatMap.get(memberId).add(aiResponseDto);
            sink.tryEmitNext(aiResponse);
            
            // 2. SSE 응답 전송     
            log.info("SSE AI Response Success! [memberId : "+memberId+"]");
        }
        
        log.info("AI Check Success! [memberId : "+memberId+"]");             
                           
        
        // 3. 리포트일 경우 DB 저장
        if (AiAnswerType == AnswerType.R) {
        	
            //채팅내역 요약
            String userPromptSummary = buildUserPrompt(tempChatMap.get(memberId), "Summary");
            aiRequestDto.setMessage(userPromptSummary);
            SummaryAiResponse summary = aiChatService.ResponseSummaryMessage(aiRequestDto);
        	        	
            Long roadId =saveToDB(memberId, tempChatMap.get(memberId),summary, reportData);
              
            //roadId 전달
        	sink.tryEmitNext("roadId : "+roadId);  
            
            //채팅내역 초기화
            tempChatMap.put(memberId, new ArrayList<>());
            
            log.info("Chatting List Save Success! [memberId : "+memberId+"]");                     			
            
        }
    }
	 
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
	    			.conclusion(reportData.getConclusion())
	    			.thinkinContent(reportData.getThinkingContent())
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
	    	userPrompt.append("이 대화를 바탕으로 보고서를 작성해주세요.");
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
	
	public void disconnect(Long memberId) {
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
}
