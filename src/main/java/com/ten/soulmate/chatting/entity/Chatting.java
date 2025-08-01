package com.ten.soulmate.chatting.entity;

import java.time.LocalDateTime;
import java.util.List;
import com.ten.soulmate.member.entity.Member;
import com.ten.soulmate.road.entity.Road;
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
@Table(name = "Chatting")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chatting {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Column(nullable = false)
    private LocalDateTime createAt;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<Road> roads;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL)
    private List<ChattingList> chattingLists;
}
