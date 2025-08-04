package com.ten.soulmate.member.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ten.soulmate.member.entity.Member;
import com.ten.soulmate.member.entity.MemberAttribute;

public interface MemberAttributeRepository extends JpaRepository<MemberAttribute, Long>{

	Optional<MemberAttribute> findByMemberId(Long memberId);
}
