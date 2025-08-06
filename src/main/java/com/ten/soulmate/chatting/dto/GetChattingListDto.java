package com.ten.soulmate.chatting.dto;

import java.time.LocalDateTime;
import com.ten.soulmate.global.type.ChatType;
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
public class GetChattingListDto {

	@Schema(description = "채팅 주체(A : AI, M: 사용자)", example = "M")
	ChatType chatType;
	
	@Schema(description = "메시지", example = "나 요즘 퇴사를 할지 고민이야.")
	String message;
	
	@Schema(description = "채팅 발송 시간", example = "2025-08-06 13:14:43.304632")
    private LocalDateTime createAt;

}
