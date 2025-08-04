package com.ten.soulmate.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class LoginResponseDto {
	
	@Schema(description = "회원 ID(회원 테이블의 pk)", example = "1")
	Long memberId;
	
	@Schema(description = "회원 상태(신규 회원인지 아닌지)", example = "Y")
	String newMemberYn;

}
