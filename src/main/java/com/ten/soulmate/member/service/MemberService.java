package com.ten.soulmate.member.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.ten.soulmate.chatting.dto.AiRequestDto;
import com.ten.soulmate.chatting.service.AiChatService;
import com.ten.soulmate.global.dto.ResponseDto;
import com.ten.soulmate.member.dto.TodayAdivceResponseDto;
import com.ten.soulmate.member.dto.UpdateSoulmateNameDto;
import com.ten.soulmate.member.entity.Member;
import com.ten.soulmate.member.entity.MemberAttribute;
import com.ten.soulmate.member.repository.MemberAttributeRepository;
import com.ten.soulmate.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
	
	private final MemberRepository memberRepository; 
	private final MemberAttributeRepository memberAttributeRepository;
	private final AiChatService aiChatService;
	
	@Transactional
	public ResponseEntity<?> updateSoulmateName(UpdateSoulmateNameDto request)
	{		
		ResponseDto response = new ResponseDto();		
		try {
			
			memberRepository.updateSoulMateName(request.getSoulmateName(), request.getMemberId());
			
			log.info("Update SoulmateName Success!");
			
			response.setMessage("Success");
			return ResponseEntity.ok(response);
			
		} catch(Exception e)
		{
			log.error("Update SoulmateName Error : "+e.getMessage());
			response.setMessage("Failed");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
				
	}
	
	@Transactional
	public ResponseEntity<?> deleteMember(Long memberId)
	{
		ResponseDto response = new ResponseDto();		
		try {
			
			memberRepository.deleteById(memberId);
			
			log.info("Delete Member Success! [memberId : "+memberId+"]");
			response.setMessage("Success");
			
			return ResponseEntity.ok(response);
		} catch(Exception e)
		{
			log.error("Delete Member Error : "+e.getMessage());
			response.setMessage("Failed");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}
	
	public ResponseEntity<?> createTodayAdvice(Long memberId){
		ResponseDto response = new ResponseDto();		
		try {
			
			Optional<MemberAttribute> memberAttribute = memberAttributeRepository.findByMemberId(memberId);
			
			AiRequestDto aiRequestDto = AiRequestDto.builder()
										.valueAttribute(memberAttribute.get().getValueAttribute())
										.decision(memberAttribute.get().getDecision())
										.regret(memberAttribute.get().getRegret())
										.decisionTrust(memberAttribute.get().getDecisionTrust())
										.build();										
					
			String advice = aiChatService.ResponseAdviceMessage(aiRequestDto);
			
			if(advice.equals("error"))
			{
				log.error("AI Create Advice Error");
				response.setMessage("Failed");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);				
			}else {
				TodayAdivceResponseDto res = TodayAdivceResponseDto.builder()
						.advice(advice).build();				
				return ResponseEntity.ok(res);
			}			
			
		} catch (Exception e) {
			log.error("Create Adivce Error : "+e.getMessage());
			response.setMessage("Failed");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		
	}
}
