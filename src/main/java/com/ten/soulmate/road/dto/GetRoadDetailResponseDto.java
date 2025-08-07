package com.ten.soulmate.road.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetRoadDetailResponseDto {
	
	@Schema(description = "추론 내용", example = "AI의 추론 내용")
	private String thinkingContent;
	
	@Schema(description = "A안 제목", example = "퇴사하기")
	private String titleA;
	
	@Schema(description = "B안 제목", example = "그냥다니기")
	private String titleB;
	
	@Schema(description = "A안 상세", example = "✅ A안: 퇴사\r\n"
			+ " ✅ 가치 일치도: 높음\r\n"
			+ " 이유 : 현재의 과도한 업무 부담과 보상 부족은 장기적 성공(경력/금전적 성장)을 저해함. 새로운 기회 탐색 시 더 나은 근로조건 확보 가능.")
	private String contentA;
	
	@Schema(description = "B안 상세", example = "❌ B안: 현직장 유지 및 협상 시도\r\n"
			+ " ❌ 가치 일치도: 중간\r\n"
			+ " 이유: 단기적 안정성은 있으나 과도한 업무 강도가 장기적 성공 방해 요소로 작용할 우려. 과거 문제 방치했다 악화된 경험 있다면 비추천.")
	private String contentB;
	
	@Schema(description = "결론 제목", example = "소울메이트는 A안을 추천합니다.")
	private String coclusionTitle;
	
	@Schema(description = "결론", example = "홍길동님의 핵심 가치는 **성공**이며,\r\n"
			+ " 성향상 신중하나 현재 업무 환경의 구조적 문제가 지속된다면 **A안(퇴사)**이 후회 가능성을 낮추는 선택입니다.\r\n"
			+ " 단, 이직 전 구체적 계획 수립 필수.")
	private String conclusion;
	
	@Schema(description = "사용자의 선택 결과", example = "A")
	private String result;
	
	@Schema(description = "회고", example = "퇴사를 하기로 결심했습니다.")
	private String review;
		
}
