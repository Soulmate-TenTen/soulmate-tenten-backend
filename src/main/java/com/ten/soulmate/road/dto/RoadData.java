package com.ten.soulmate.road.dto;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;



@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoadData {

	@Schema(description = "기로 pk값", example = "1")
	private Long id;
	
	@Schema(description = "AI가 요약한 대화 내역", example = "일도 잘 맞고 월급도 적고 퇴사가 하고싶어요.")
    private String summary;
	
	@Schema(description = "기로의 상태(미선택/선택안료/회고완료)", example = "미선택")
    private String roadStatus;
	
	@Schema(description = "AI가 지정한 기로의 제목", example = "퇴사가 하고싶아요.")
    private String title;
	
	@Schema(description = "기로의 생성일", example = "2025-08-04T12:04:13.747Z")
    private LocalDateTime createAt;

}
