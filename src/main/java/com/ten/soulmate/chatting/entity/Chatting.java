package com.ten.soulmate.chatting.entity;

import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "chatting")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Chatting {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @CreationTimestamp
    @Column(nullable = false, name="createAt")
    private LocalDateTime createAt;

    @Column(length = 10, name="finYn")
    private String finYn;
    
    @OneToMany(mappedBy = "chatting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChattingList> chattingLists;

    @OneToMany(mappedBy = "chatting", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Road> roads;
}
