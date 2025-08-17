package com.ten.soulmate.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ten.soulmate.member.entity.MemberAdvice;

public interface MemberAdviceRepository extends JpaRepository<MemberAdvice, Long> {

	Optional<MemberAdvice> findByMemberId(Long memberId);
}
