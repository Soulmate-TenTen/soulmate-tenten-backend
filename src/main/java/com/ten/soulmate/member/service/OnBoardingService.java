package com.ten.soulmate.member.service;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ten.soulmate.global.dto.ResponseDto;
import com.ten.soulmate.member.dto.OnBoardingDto;
import com.ten.soulmate.member.entity.MemberAttribute;
import com.ten.soulmate.member.repository.MemberAttributeRepository;
import com.ten.soulmate.member.repository.MemberRepository;
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
		
		ResponseDto response = new ResponseDto();
		
		try {			
					
			Optional<MemberAttribute> ExisitedMember = memberAttributeRepository.findByMemberId(request.getMemberId());
			
			//update - 온보딩 수정
			if(ExisitedMember.isPresent())
			{			
				MemberAttribute memberAttribute = MemberAttribute.builder()
													.id(ExisitedMember.get().getId())
													.member(memberRepository.findById(request.getMemberId()).get())
													.valueAttribute(request.getValueAttribute())
													.decision(request.getDecision())
													.regret(request.getRegret())
													.decisionTrust(request.getDecisionTrust())
													.build();
				memberAttributeRepository.save(memberAttribute);
				
				log.info("Onboarding Update Success");				
			}
			//insert - 최초의 온보딩
			else {
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
			}
									
			response.setMessage("Success");
			
			return ResponseEntity.ok(response);			
		}catch(Exception e)
		{
			log.error("Set OnBoarding Error : "+ e.getMessage());
			response.setMessage("Failed");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
		
		
	}
	
}
