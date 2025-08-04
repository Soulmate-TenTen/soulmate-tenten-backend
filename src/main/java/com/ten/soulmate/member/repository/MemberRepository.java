package com.ten.soulmate.member.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.ten.soulmate.member.entity.Member;

public interface MemberRepository extends JpaRepository<Member, Long>{

	Optional<Member> findByName(String name);
	
	
	@Modifying
	@Query(value = "UPDATE member SET soulmateType = :type WHERE id = :memberId", nativeQuery = true)
	void updateSoulMateType(@Param("type") String type, @Param("memberId") Long memberId);
	
	
	@Modifying
	@Query(value = "UPDATE member SET soulmateName = :soulmateName WHERE id = :memberId", nativeQuery = true)
	void updateSoulMateName(@Param("soulmateName") String soulmateName, @Param("memberId") Long memberId);
}
