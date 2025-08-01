package com.ten.soulmate.chatting.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ten.soulmate.chatting.entity.Chatting;

public interface ChattingRepository extends JpaRepository<Chatting, Long>{

}
