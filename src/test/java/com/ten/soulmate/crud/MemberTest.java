package com.ten.soulmate.crud;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.ten.soulmate.global.type.MemberType;
import com.ten.soulmate.global.type.SoulMateType;
import com.ten.soulmate.member.entity.Member;
import com.ten.soulmate.member.entity.MemberAttribute;
import com.ten.soulmate.member.repository.MemberAttributeRepository;
import com.ten.soulmate.member.repository.MemberRepository;

@DataJpaTest
public class MemberTest {

	@Autowired
	private MemberRepository memberRepository;
	
	@Autowired
	private MemberAttributeRepository memberAttributeRepository;
		
	@Test
	@DisplayName("Member 및 MemberAttribute 저장과 조회 테스트")
	void saveAndFindMemberWithMemberAttribute() {
	
		Member member = Member.builder()
						.email("test@test.com")
						.pw("1234")
						.role(MemberType.USER)
						.name("테스트")
						.soulmateName("소울")
						.soulmateType(SoulMateType.F)
						.build();
		
		Member savedMember = memberRepository.save(member);
		
		MemberAttribute memberAttribute = MemberAttribute.builder()
											.member(savedMember)
											.valueAttribute("성공")
											.decision("오래 고민하고 결정을 미루는 편")
											.regret("어디서부터 잘못된 걸까")
											.decisionTrust("감정을 기반으로 해결책 제시")
											.build();
		
		MemberAttribute savedMemberAttribute = memberAttributeRepository.save(memberAttribute);
		
        assertThat(savedMember.getEmail()).isEqualTo("test@test.com");
        assertThat(savedMember.getRole()).isEqualTo(MemberType.USER);
        assertThat(savedMember.getSoulmateType()).isEqualTo(SoulMateType.F);

        
        assertThat(savedMemberAttribute.getValueAttribute()).isEqualTo("성공");
        assertThat(savedMemberAttribute.getDecision()).isEqualTo("오래 고민하고 결정을 미루는 편");
        assertThat(savedMemberAttribute.getRegret()).isEqualTo("어디서부터 잘못된 걸까");
        assertThat(savedMemberAttribute.getDecisionTrust()).isEqualTo("감정을 기반으로 해결책 제시");
		
		
	}
		
	
}
