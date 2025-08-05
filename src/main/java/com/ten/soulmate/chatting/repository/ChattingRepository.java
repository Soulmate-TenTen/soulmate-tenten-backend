package com.ten.soulmate.chatting.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ten.soulmate.chatting.entity.Chatting;

public interface ChattingRepository extends JpaRepository<Chatting, Long>{
	
	@Query("SELECT c FROM Chatting c WHERE c.member.id = :memberId AND c.finYn = 'N'")
	Optional<Chatting> findActiveChatting(@Param("memberId") Long memberId);
	
	@Modifying
	@Query(value = "UPDATE chatting SET finYn = 'Y' WHERE id = :chatId",nativeQuery = true)
	void updateFinYnToYByChatId(@Param("chatId") Long chatId);
}
