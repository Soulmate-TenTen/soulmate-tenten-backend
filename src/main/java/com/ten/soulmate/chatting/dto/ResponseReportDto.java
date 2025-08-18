package com.ten.soulmate.chatting.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResponseReportDto {

	@Schema(description = "기로 pk", example = "1")
	Long roadId;
}
