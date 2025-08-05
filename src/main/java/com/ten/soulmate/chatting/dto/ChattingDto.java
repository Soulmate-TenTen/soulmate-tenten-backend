package com.ten.soulmate.chatting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChattingDto {
	
	@Schema(description = "사용자 pk", example = "1")
	private Long memberId;
	
	@Schema(description = "사용자의 질문", example = "나 요즘 퇴사를 할지 고민이야.")
	private String question;

}
