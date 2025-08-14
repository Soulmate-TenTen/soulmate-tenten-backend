package com.ten.soulmate.chatting.dto;

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
public class ReportAiResponse {
	
	private String thinkingContent;
	
	private String titleA;
	private String titleB;
	private String answerA;
	private String answerB;
	
	private String recommend;
	private String conclusion;
	private String titleConclusion;
	private String category;

	
}
