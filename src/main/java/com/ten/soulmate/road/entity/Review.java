package com.ten.soulmate.road.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.ten.soulmate.chatting.entity.ChattingList;
import com.ten.soulmate.member.entity.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "review")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Review {
	 	@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne(optional = false)
	    @JoinColumn(name = "roadId", nullable = false)
	    private Road road;

	    @ManyToOne(optional = false)
	    @JoinColumn(name = "memberId", nullable = false)
	    private Member member;

	    @Column(columnDefinition = "TEXT")
	    private String content;

	    @Column(length = 10, name="result")
	    private String result;

	    @CreationTimestamp
	    @Column(nullable = false, name="createAt")
	    private LocalDateTime createAt;
}

