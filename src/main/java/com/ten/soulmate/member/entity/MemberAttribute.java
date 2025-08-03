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
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "memberattribute")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberAttribute {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "memberId", nullable = false)
    private Member member;

    @Column(length = 20, nullable = false, name="valueAttribute")
    private String valueAttribute;

    @Column(length = 200, nullable = false, name="decision")
    private String decision;

    @Column(length = 200, nullable = false, name="regret")
    private String regret;

    @Column(length = 200, nullable = false, name="decisionTrust")
    private String decisionTrust;
}
