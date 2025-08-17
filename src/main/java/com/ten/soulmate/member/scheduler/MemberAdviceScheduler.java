package com.ten.soulmate.member.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ten.soulmate.member.service.MemberService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberAdviceScheduler {
	
	private final MemberService memberService;
	
	@Scheduled(cron = "0 0 0 * * *")
	//@Scheduled(cron = "0 8 17 * * *")
	@Transactional
    public void updateAdviceEveryMidnight() {
        memberService.updateAdvice();
    }
	
}
