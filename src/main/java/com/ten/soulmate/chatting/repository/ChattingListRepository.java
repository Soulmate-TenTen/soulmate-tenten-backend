package com.ten.soulmate.chatting.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ten.soulmate.chatting.entity.ChattingList;

public interface ChattingListRepository extends JpaRepository<ChattingList, Long>{
	
	List<ChattingList> findByChattingId(Long chatId);
	
}
