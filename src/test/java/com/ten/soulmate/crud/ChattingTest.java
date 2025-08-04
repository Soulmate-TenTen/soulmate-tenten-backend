package com.ten.soulmate.crud;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import com.ten.soulmate.chatting.entity.Chatting;
import com.ten.soulmate.chatting.entity.ChattingList;
import com.ten.soulmate.chatting.repository.ChattingListRepository;
import com.ten.soulmate.chatting.repository.ChattingRepository;
import com.ten.soulmate.global.type.AnswerType;
import com.ten.soulmate.global.type.ChatType;
import com.ten.soulmate.global.type.MemberType;
import com.ten.soulmate.global.type.SoulMateType;
import com.ten.soulmate.member.entity.Member;
import com.ten.soulmate.member.repository.MemberRepository;
import jakarta.persistence.EntityManager;

@DataJpaTest
public class ChattingTest {

	@Autowired
	private ChattingRepository chattingRepository;
	
	@Autowired
	private ChattingListRepository chattingListRepository;
	
	@Autowired 
	private MemberRepository memberRepository;
	
	@Autowired
	EntityManager em;
	
	@Test
	@DisplayName("Chatting 및 ChattingList 저장과 조회, 업데이트 테스트")
	void saveAndFindChattingWithChattingList() {
		
		Member member = Member.builder()
				.email("test@test.com")
				.pw("1234")
				.role(MemberType.USER)
				.name("테스트")
				.soulmateName("소울")
				.soulmateType(SoulMateType.F)
				.build();

		Member savedMember = memberRepository.save(member);
		
		
		Chatting chatting = Chatting.builder()
							.member(savedMember)
							.build();
		
		Chatting savedChatting = chattingRepository.save(chatting);
		
				
		ChattingList chattingList1 = ChattingList.builder()
									.chatting(savedChatting)
									.member(savedMember)
									.message("나 퇴사를 할 지 고민이야")
									.answerType(AnswerType.N)
									.chatType(ChatType.M)
									.finYn("N")
									.build();
		chattingListRepository.save(chattingList1);
		
		ChattingList chattingList2 = ChattingList.builder()
				.chatting(savedChatting)
				.member(savedMember)
				.message("뭐 때문에 그러시죠?")
				.answerType(AnswerType.N)
				.chatType(ChatType.A)
				.finYn("N")
				.build();
		chattingListRepository.save(chattingList2);

		
		ChattingList chattingList3 = ChattingList.builder()
				.chatting(savedChatting)
				.member(savedMember)
				.message("직무가 나랑 안맞아.")
				.answerType(AnswerType.N)
				.chatType(ChatType.M)
				.finYn("N")
				.build();
		
		chattingListRepository.save(chattingList3);

		ChattingList chattingList4 = ChattingList.builder()
				.chatting(savedChatting)
				.member(savedMember)
				.message("리포트 입니다......")
				.answerType(AnswerType.R)
				.chatType(ChatType.A)
				.finYn("N")
				.build();
		
		chattingListRepository.save(chattingList4);

		chattingListRepository.updateFinYnNative(savedChatting.getId(), "Y");		
		em.clear();
		
		List<ChattingList> savedChattingList = chattingListRepository.findByChattingId(savedChatting.getId());
		
		
		for(ChattingList chatList : savedChattingList)
		{
			assertThat(chatList.getFinYn()).isEqualTo("Y");
		}
				
		
		for(int i=0; i<savedChattingList.size(); i++)
		{
			switch(i) {
			case 0: 
				assertThat(savedChattingList.get(i).getMessage()).isEqualTo("나 퇴사를 할 지 고민이야");
				break;
			case 1:
				assertThat(savedChattingList.get(i).getMessage()).isEqualTo("뭐 때문에 그러시죠?");
				break;
			case 2:
				assertThat(savedChattingList.get(i).getMessage()).isEqualTo("직무가 나랑 안맞아.");
				break;
			case 3:
				assertThat(savedChattingList.get(i).getMessage()).isEqualTo("리포트 입니다......");
				break;
			}
			
		}
		
	}
			
}
