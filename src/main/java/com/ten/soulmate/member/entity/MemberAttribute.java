package com.ten.soulmate.member.entity;

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
import lombok.NoArgsConstructor;


@Entity
@Table(name = "MemberAttribute")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Column(nullable = false, length = 20)
    private String value;

    @Column(nullable = false, length = 200)
    private String decision;

    @Column(nullable = false, length = 200)
    private String regret;

    @Column(name = "Field4", nullable = false, length = 400)
    private String field4;

    @Column(nullable = false, length = 200)
    private String decisionTrust;

    @Column(nullable = false, length = 400)
    private String decisionPast;
}
