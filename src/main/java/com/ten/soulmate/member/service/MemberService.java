package com.ten.soulmate.member.service;

import java.util.List;
import java.util.Optional;

import com.google.common.util.concurrent.RateLimiter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;

import com.ten.soulmate.chatting.dto.AiRequestDto;
import com.ten.soulmate.chatting.service.AiChatService;
import com.ten.soulmate.global.dto.ResponseDto;
import com.ten.soulmate.member.dto.TodayAdivceResponseDto;
import com.ten.soulmate.member.dto.UpdateSoulmateNameDto;
import com.ten.soulmate.member.entity.Member;
import com.ten.soulmate.member.entity.MemberAdvice;
import com.ten.soulmate.member.entity.MemberAttribute;
import com.ten.soulmate.member.repository.MemberAdviceRepository;
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
	private final MemberAdviceRepository memberAdviceRepository;
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
	
	public ResponseEntity<?> selectTodayAdvice(Long memberId){
		ResponseDto response = new ResponseDto();		
		try {
			
			Optional<MemberAdvice> memberAdvice = memberAdviceRepository.findByMemberId(memberId);
						
			if(memberAdvice.isPresent())
			{
				TodayAdivceResponseDto res = TodayAdivceResponseDto.builder()
						.advice(memberAdvice.get().getAdvice()).build();	
				
				return ResponseEntity.ok(res);
			}else {
				log.error("Not Found MemberAdvice");
				response.setMessage("Not Found MemberAdvice");
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
			}
		
		} catch (Exception e) {
			log.error("Select Adivce Error : "+e.getMessage());
			response.setMessage("Failed");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
				
	}
	
	@Transactional
	public void updateAdvice() {
	    try {
	        int page = 0;
	        Page<MemberAttribute> pageResult;

	        // RateLimiter 초당 0.4건 (QPM=60 기준 여유두기)
	        RateLimiter limiter = RateLimiter.create(0.4);

	        do {
	            pageResult = memberAttributeRepository.findAll(PageRequest.of(page, 100));
	            for (MemberAttribute attr : pageResult) {
	                try {
	                    limiter.acquire(); // 속도 제어
	                    safeUpdateMemberAdvice(attr); // 백오프 재시도 적용
	                } catch (Exception e) {
	                    log.error("Advice update failed for memberId={}", attr.getMember().getId(), e);
	                }
	            }
	            page++;
	        } while (pageResult.hasNext());

	    } catch (Exception e) {
	        log.error("Today Update Advice ERROR", e);
	    }
	}

	// 429 대응 재시도 로직
	private void safeUpdateMemberAdvice(MemberAttribute attr) throws InterruptedException {
	    int retries = 5;
	    int delay = 2000; // 2초 시작

	    for (int i = 0; i < retries; i++) {
	        try {
	            updateMemberAdvice(attr);
	            return;
	        } catch (HttpClientErrorException.TooManyRequests e) {
	            log.warn("429 for memberId={}, retry in {}s", attr.getMember().getId(), delay / 1000);
	            Thread.sleep(delay);
	            delay *= 2; // 2 -> 4 -> 8 -> 16초 (최대)
	        }
	    }
	    log.error("Advice update failed after retries for memberId={}", attr.getMember().getId());
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void updateMemberAdvice(MemberAttribute memberAttribute) {
	    AiRequestDto dto = AiRequestDto.builder()
	            .valueAttribute(memberAttribute.getValueAttribute())
	            .decision(memberAttribute.getDecision())
	            .regret(memberAttribute.getRegret())
	            .decisionTrust(memberAttribute.getDecisionTrust())
	            .build();

	    String advice = aiChatService.ResponseAdviceMessage(dto);

	    if ("error".equals(advice)) {
	        log.warn("AI Create Advice Error for memberId={}", memberAttribute.getMember().getId());
	        return;
	    }

	    Long memberId = memberAttribute.getMember().getId();

	    memberAdviceRepository.findByMemberId(memberId).ifPresentOrElse(
	        memberAdvice -> {
	            memberAdvice.setAdvice(advice);  // dirty checking으로 update
	            log.info("Advice updated for memberId={}", memberId);
	        },
	        () -> {
	            MemberAdvice newAdvice = MemberAdvice.builder()
	                    .member(memberAttribute.getMember())
	                    .advice(advice)
	                    .build();
	            memberAdviceRepository.save(newAdvice);
	            log.info("Advice created for memberId={}", memberId);
	        }
	    );
	}

}
