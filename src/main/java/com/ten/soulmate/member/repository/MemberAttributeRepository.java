package com.ten.soulmate.member.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.ten.soulmate.member.entity.Member;

public interface MemberAttributeRepository extends JpaRepository<Member, Long>{

}
