package com.ten.soulmate.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateSoulmateNameDto {
	
	@Schema(description = "회원 ID(회원 테이블의 pk)", example = "1")
	Long memberId;
	
	@Schema(description = "변경할 소울메이트 이름", example = "소울이")
	String soulmateName;

}
