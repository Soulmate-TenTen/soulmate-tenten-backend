package com.ten.soulmate.member.service;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.ten.soulmate.global.type.SoulMateType;
import com.ten.soulmate.member.dto.OnBoardingDto;
import com.ten.soulmate.member.entity.MemberAttribute;
import com.ten.soulmate.member.repository.MemberAttributeRepository;
import com.ten.soulmate.member.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class OnBoardingService {

	private final MemberRepository memberRepository;
	private final MemberAttributeRepository memberAttributeRepository;
	
	
	
	
	@Transactional
	public ResponseEntity<?> setOnBoardingResult(OnBoardingDto request)
	{		
		
		
		System.out.println(request.toString());
		
		try {
			
			System.out.println("type: "+request.getSoulmateType());
			
			memberRepository.updateSoulMateType(request.getSoulmateType().name(), request.getMemberId());
			
			MemberAttribute memberAttribute = MemberAttribute.builder()
												.member(memberRepository.findById(request.getMemberId()).get())
												.valueAttribute(request.getValueAttribute())
												.decision(request.getDecision())
												.regret(request.getRegret())
												.decisionTrust(request.getDecisionTrust())
												.build();
			
			memberAttributeRepository.save(memberAttribute);
			
			log.info("Onboarding Insert Success");
			
			return ResponseEntity.ok(Map.of("meesage","Success"));			
		}catch(Exception e)
		{
			log.error("Set OnBoarding Error : "+ e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("meesage","Failed"));
		}
		
		
	}
	
}
