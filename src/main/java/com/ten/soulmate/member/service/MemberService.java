package com.ten.soulmate.member.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ten.soulmate.global.dto.ResponseDto;
import com.ten.soulmate.member.dto.UpdateSoulmateNameDto;
import com.ten.soulmate.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
	
	private final MemberRepository memberRepository; 
	
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
			log.error("Update SoulmateName Success!");
			response.setMessage("Failed");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
				
	}

}
