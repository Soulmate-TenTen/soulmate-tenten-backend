package com.ten.soulmate.chatting.dto;

import java.time.LocalDateTime;
import com.ten.soulmate.chatting.entity.Chatting;
import com.ten.soulmate.global.type.AnswerType;
import com.ten.soulmate.global.type.ChatType;
import com.ten.soulmate.member.entity.Member;
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
public class ChattingListDto {

	private Chatting chatting;
    private Member member;
    private String message;
    private LocalDateTime createAt;
    private AnswerType answerType;
    private ChatType chatType;
    
}
