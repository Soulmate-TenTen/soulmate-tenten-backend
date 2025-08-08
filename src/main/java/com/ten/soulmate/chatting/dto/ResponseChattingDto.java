package com.ten.soulmate.chatting.dto;

import com.ten.soulmate.global.type.AnswerType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseChattingDto {

	@Schema(description = "응답 타입(N/R) - N : 일반 응답, R : REPORT 생성 응답", example = "N")
	AnswerType answerType;
	
	@Schema(description = "AI 응답", example = "홍길동 님, 정말 힘드실 것 같아요. 혹시 회사 측과 추가 수당에 대해 논의해보신 적이 있나요? 그리고 현재 상황에서 벗어나기 위해 어떤 방법을 고려 중이신가요?")
	String message;
	
	@Schema(description = "기로 pk, 리포트 생성 시에 만 값이 있음, 나머지는 null", example = "1")
	Long roadId;
}
