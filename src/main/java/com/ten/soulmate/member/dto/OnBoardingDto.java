package com.ten.soulmate.member.dto;

import com.ten.soulmate.global.type.SoulMateType;

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

	private Long memberId;
	private String valueAttribute;
	private String decision;
	private String regret;
	private String decisionTrust;
	private SoulMateType soulmateType;
	
}
