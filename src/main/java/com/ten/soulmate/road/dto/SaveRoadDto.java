package com.ten.soulmate.road.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class SaveRoadDto {

	@Schema(description = "기로 pk값", example = "1")
	private Long id;	
	
	@Schema(description = "사용자의 선택 결과", example = "A")
	String result;
	
	@Schema(description = "사용자가 작성한 회고", example = "저는 퇴사를 하기로 결심했습니다.")
	String review;
	
}
