package com.ten.soulmate.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginDto {
	
	@Schema(description = "회원 이메일(카카오 회원 이메일)", example = "test@test.com")
	private String email;
	
	@Schema(description = "회원이름(카카오 회원이름)", example = "홍길동")
	private String name;
	
}
