package com.ten.soulmate.road.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ten.soulmate.chatting.entity.Chatting;
import com.ten.soulmate.road.entity.Road;

public interface RoadRepository extends JpaRepository<Road, Long>{
	
	@Query(value = "SELECT DISTINCT DAY(createAt) " +
            "FROM road " +
            "WHERE MONTH(createAt) = :month " +
            "AND YEAR(createAt) = :year "
            + "AND memberId = :memberId", nativeQuery = true)
	List<Integer> findExistRoadDay(@Param("year") int year, @Param("month") int month, @Param("memberId")Long memberId);


	@Query(value = "SELECT * FROM road " +
            "WHERE memberId = :memberId " +
            "AND DATE(createAt) = :targetDate " +
            "ORDER BY roadStatus ASC, createAt DESC", nativeQuery = true)
	List<Road> findRoadList(@Param("memberId") Long memberId, @Param("targetDate") LocalDate targetDate); 

	@Query("SELECT COUNT(r) FROM Road r WHERE r.member.id = :memberId")
	long countByMemberId(@Param("memberId") Long memberId);

    Optional<Road> findByChatting(Chatting chatting);
	
}