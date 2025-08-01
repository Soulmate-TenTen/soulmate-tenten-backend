package com.ten.soulmate.road.entity;

import java.time.LocalDateTime;
import java.util.List;

import com.ten.soulmate.chatting.entity.Chatting;
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
import lombok.NoArgsConstructor;


@Entity
@Table(name = "Road")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Road {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "chatId", nullable = false)
    private Chatting chat;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Column
    private LocalDateTime createAt;

    @Column
    private LocalDateTime updateAt;

    @OneToMany(mappedBy = "road", cascade = CascadeType.ALL)
    private List<Review> reviews;
}

