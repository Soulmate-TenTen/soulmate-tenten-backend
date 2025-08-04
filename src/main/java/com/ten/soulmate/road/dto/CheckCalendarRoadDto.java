package com.ten.soulmate.road.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckCalendarRoadDto {

	 @Schema(description = "사용자 pk값", example = "1")
	 private Long memberId;
	 
	 @Schema(description = "켈린터 년,월", example = "2025-08")
	 private String selectMonth;

}
