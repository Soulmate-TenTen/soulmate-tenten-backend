package com.ten.soulmate.road.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.ten.soulmate.chatting.entity.Chatting;
import com.ten.soulmate.chatting.entity.ChattingList;
import com.ten.soulmate.member.entity.Member;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "road")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Road {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chatListId")
    private ChattingList chatList;

    @ManyToOne(optional = false)
    @JoinColumn(name = "chatId", nullable = false)
    private Chatting chatting;

    @ManyToOne(optional = false)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @CreationTimestamp
    private LocalDateTime createAt;

    @UpdateTimestamp
    private LocalDateTime updateAt;

    @Column(length = 500, name="summary")
    private String summary;

    @Column(length = 100, name="answerA")
    private String answerA;

    @Column(length = 100, name="answerB")
    private String answerB;

    @Column(length = 50, name="result")
    private String result;
    
    @Column(name="roadStatus")
    private int roadStatus;
    
    @Column(length = 200, name="title")
    private String title;
    
    @OneToMany(mappedBy = "road", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews;
}

