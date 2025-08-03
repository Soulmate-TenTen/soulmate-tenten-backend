package com.ten.soulmate.chatting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ten.soulmate.chatting.entity.ChattingList;

public interface ChattingListRepository extends JpaRepository<ChattingList, Long>{
	
	List<ChattingList> findByChattingId(Long chatId);

	@Modifying
	@Query(value = "UPDATE chattinglist SET finYn = :finYn WHERE chatId = :chatId", nativeQuery = true)
	void updateFinYnNative(@Param("chatId") Long chatId, @Param("finYn") String finYn);
	
}
