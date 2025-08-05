package com.ten.soulmate.chatting.dto;

import com.ten.soulmate.global.type.SoulMateType;
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
public class AiRequestDto {

	String message;
	
	//프롬프트에 대입할 사용자명, 소울메이트명
	String memberName;
	String soulmateName;
	
	//온보딩 검사결과
	String valueAttribute;
	String decision;
	String regret;
	String decisionTrust;
	SoulMateType soulMateType;
	
}
