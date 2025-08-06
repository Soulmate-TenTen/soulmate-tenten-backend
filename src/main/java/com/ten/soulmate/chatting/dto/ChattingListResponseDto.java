package com.ten.soulmate.chatting.dto;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChattingListResponseDto {

	@Schema(description = "대화 내용 데이터 리스트", example = "{\r\n"
			+ "  \"chattingList\": [\r\n"
			+ "    {\r\n"
			+ "      \"chatType\": \"M\",\r\n"
			+ "      \"message\": \"나 요즘 퇴사를 할지 고민이야.\",\r\n"
			+ "      \"createAt\": \"2025-08-06T15:33:23.958051\"\r\n"
			+ "    },\r\n"
			+ "    {\r\n"
			+ "      \"chatType\": \"A\",\r\n"
			+ "      \"message\": \"홍길동 님, 퇴사 결정은 중요한 문제이기 때문에 신중하게 고려해야 합니다. 현재 직장에서 느끼는 불만족스러운 점이나 퇴사를 고려하게 된 구체적인 이유가 있으신가요? 그리고 앞으로 어떤 계획을 가지고 계신지 말씀해 주시면 더 나은 조언을 드릴 수 있을 것 같습니다.\",\r\n"
			+ "      \"createAt\": \"2025-08-06T15:33:23.97757\"\r\n"
			+ "    },\r\n"
			+ "    {\r\n"
			+ "      \"chatType\": \"M\",\r\n"
			+ "      \"message\": \"매일 야근하고 주말에도 출근하는데 포괄임금제라 추가 수당도 없고 너무 힘들어.\",\r\n"
			+ "      \"createAt\": \"2025-08-06T15:33:23.979572\"\r\n"
			+ "    },\r\n"
			+ "    {\r\n"
			+ "      \"chatType\": \"A\",\r\n"
			+ "      \"message\": \"REPORT\",\r\n"
			+ "      \"createAt\": \"2025-08-06T15:33:23.981571\"\r\n"
			+ "    }\r\n"
			+ "  ]\r\n"
			+ "}")
	List<GetChattingListDto> chattingList;
}
