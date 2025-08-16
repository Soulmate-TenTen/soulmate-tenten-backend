package com.ten.soulmate.member.dto;

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
public class TodayAdivceResponseDto {

	@Schema(description = "오늘의 조언", example = "시작이 반이다.")
	private String advice;
}
