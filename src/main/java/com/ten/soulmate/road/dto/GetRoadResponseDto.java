package com.ten.soulmate.road.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GetRoadResponseDto {

	@Schema(description = "기로가 존재하는 날짜(캘린더 표시용)", example = "[1, 13, 21]")
	private List<Integer> existsRoadDay;
	
	@Schema(description = "해당 날짜의 기로 리스트")
	private List<RoadData> roadList;
	
}
