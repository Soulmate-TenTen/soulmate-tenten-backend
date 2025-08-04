package com.ten.soulmate.auth.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.ten.soulmate.auth.dto.LoginDto;
import com.ten.soulmate.auth.dto.LoginResponseDto;
import com.ten.soulmate.chatting.entity.Chatting;
import com.ten.soulmate.chatting.repository.ChattingRepository;
import com.ten.soulmate.global.dto.ResponseDto;
import com.ten.soulmate.global.type.MemberType;
import com.ten.soulmate.member.entity.Member;
import com.ten.soulmate.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

	private final MemberRepository memberRepository;
	private final ChattingRepository chattingRepository;
	
	public ResponseEntity<?> login(LoginDto request)
	{				
		LoginResponseDto response = new LoginResponseDto();

			
		try {
			
			Optional<Member> member = memberRepository.findByEmail(request.getEmail());
			if(!member.isPresent())
			{
				Member newMember = Member.builder()
									.email(request.getEmail())
									.pw("kakao")
									.name(request.getName())
									.role(MemberType.USER)
									.build();
				
				response.setMemberId(memberRepository.saveAndFlush(newMember).getId());
				response.setNewMemberYn("Y");
				
				//모든 사용자는 첫 로그인 시 채팅방이 하나 생성되어야 한다.
				Chatting chatting = Chatting.builder()
									.member(newMember).build();
				chattingRepository.save(chatting);
				
			}else {
				response.setMemberId(member.get().getId());
				response.setNewMemberYn("N");
			}
			
			log.info("Login Success");
			
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			
			log.error("Login Error : "+e.getMessage());
			ResponseDto res = new ResponseDto();
			res.setMessage("Failed");
			
			return ResponseEntity.status(401).body(res);
		}		
	}

}
