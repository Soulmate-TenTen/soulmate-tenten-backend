package com.ten.soulmate.road.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RoadCountResponseDto {
	
	
	@Schema(description = "기로 수(선택 횟수)", example = "5")
	long roadCount;
	
}
