package com.ten.soulmate.member.dto;

import com.ten.soulmate.global.type.SoulMateType;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OnBoardingDto {

	@Schema(description = "회원 ID(회원 테이블의 pk)", example = "1")
	private Long memberId;
	
	@Schema(description = "온보딩 1번 질문의 답", example = "성공")
	private String valueAttribute;
	
	@Schema(description = "온보딩 2번 질문의 답", example = "오래 고민하고 결정을 미루는 편")
	private String decision;
	
	@Schema(description = "온보딩 3번 질문의 답", example = "어디서부터 잘못된걸까")
	private String regret;
	
	@Schema(description = "온보딩 4번 질문의 답", example = "과거 경험")
	private String decisionTrust;
	
	@Schema(description = "온보딩 1번 질문의 답", example = "T")
	private SoulMateType soulmateType;
	
}
