package com.ten.soulmate.chatting.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import com.ten.soulmate.chatting.dto.ChattingDto;
import com.ten.soulmate.chatting.dto.ChattingListDto;
import com.ten.soulmate.chatting.entity.Chatting;
import com.ten.soulmate.chatting.entity.ChattingList;
import com.ten.soulmate.chatting.repository.ChattingListRepository;
import com.ten.soulmate.chatting.repository.ChattingRepository;
import com.ten.soulmate.global.type.AnswerType;
import com.ten.soulmate.global.type.ChatType;
import com.ten.soulmate.member.entity.Member;
import com.ten.soulmate.member.repository.MemberRepository;
import jakarta.transaction.Transactional;
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
	
	//SSE 연결 요청
	public Flux<String> connect(@RequestParam Long memberId){    	
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
        String aiResponse = "";
        
        //여기에 정보량 판단 로직 추가하고 판단 결과를 담아야함
        AnswerType AiAnswerType = null;
        
        ChattingListDto aiResponseDto = ChattingListDto.builder()
                .chatting(null)
                .member(null)
                .message(aiResponse)
                .createAt(LocalDateTime.now())
                .answerType(AiAnswerType)
                .chatType(ChatType.A)
                .build();
        
        
        tempChatMap.get(memberId).add(aiResponseDto);
        
        // 2. SSE 응답 전송
        sink.tryEmitNext(aiResponse);
        
        log.info("SSE AI Response Success! [memberId : "+memberId+"]");
        
        
        // 3. 리포트일 경우 DB 저장 + SSE 종료
        if (aiResponseDto.getAnswerType() == AnswerType.R) {
            saveToDB(memberId, tempChatMap.get(memberId));
            userSinkMap.remove(memberId);
            tempChatMap.remove(memberId);
            sink.tryEmitComplete(); // SSE 종료
            
            log.info("Chatting List Save Success! [memberId : "+memberId+"]");
        }
    }
	 
	@Transactional
	private void saveToDB(Long memberId, List<ChattingListDto> chatList) {
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
	        
    }
}
