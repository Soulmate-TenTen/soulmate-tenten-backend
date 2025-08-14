package com.ten.soulmate.road.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RemindResponseDto {
	
	@Schema(description = "리마인드 제목", example = "OO님, 3일전에 퇴사를 고민했어요.")
	String title;
	
	@Schema(description = "리마인드 유무(Y/N)", example = "Y")
	String remindYn;
	
	@Schema(description = "기로 pk값", example = "1")
	Long roadId;

}
