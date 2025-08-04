package com.ten.soulmate.crud;

import static org.assertj.core.api.Assertions.assertThat;
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
import com.ten.soulmate.road.entity.Review;
import com.ten.soulmate.road.entity.Road;
import com.ten.soulmate.road.repository.ReviewRepository;
import com.ten.soulmate.road.repository.RoadRepository;


@DataJpaTest
public class RoadTest {

	@Autowired
	RoadRepository roadRepository;
	
	@Autowired
	ReviewRepository reviewRepository;
	
	@Autowired
	MemberRepository memberRepository;
	
	@Autowired
	ChattingRepository chattingRepository;
	
	@Autowired
	ChattingListRepository chattingListRepository;
	
	@Test
	@DisplayName("Road 및 Review 저장과 조회 테스트")
	void saveAndFindRoadWithRevies()
	{
		Member member = Member.builder()
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
		
		ChattingList chattingList = ChattingList.builder()
				.chatting(savedChatting)
				.member(savedMember)
				.message("리포트 입니다......")
				.answerType(AnswerType.R)
				.chatType(ChatType.A)
				.build();
		
		chattingListRepository.save(chattingList);
		
		Road road = Road.builder()
					.chatting(savedChatting)
					.chatList(chattingList)
					.member(savedMember)
					.summary("퇴사때문에 고민을 하고 있습니다.")
					.answerA("선퇴사를 한다.")
					.answerB("이직 후 퇴사를 한다.")
					.result("긍정적인 답변")
					.title("제목")
					.roadStatus(2)
					.build();
		
		Road savedRoad = roadRepository.save(road);
		
		Review review = Review.builder()
						.road(savedRoad)
						.member(member)
						.content("소울이와의 상담을 통해 결국 선퇴사를 결정하기로 했다.")
						
						.build();
		Review savedRevies = reviewRepository.save(review);
		
		assertThat(savedRoad.getSummary()).isEqualTo("퇴사때문에 고민을 하고 있습니다.");
		assertThat(savedRoad.getAnswerA()).isEqualTo("선퇴사를 한다.");
		assertThat(savedRoad.getAnswerB()).isEqualTo("이직 후 퇴사를 한다.");
		assertThat(savedRoad.getResult()).isEqualTo("긍정적인 답변");

		assertThat(savedRevies.getContent()).isEqualTo("소울이와의 상담을 통해 결국 선퇴사를 결정하기로 했다.");
				
	}
	
	
	
	
}
