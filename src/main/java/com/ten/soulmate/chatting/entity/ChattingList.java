package com.ten.soulmate.chatting.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import com.ten.soulmate.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "ChattingList")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChattingList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chatId", nullable = false)
    private Chatting chat;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Lob
    private String question;

    @Column(nullable = false)
    private LocalDateTime questionTime;

    @Lob
    @Column(nullable = false)
    private String answer;

    @Column(nullable = false)
    private LocalDateTime answerTime;

    @Column(nullable = false, length = 10)
    private String answerType;
}

