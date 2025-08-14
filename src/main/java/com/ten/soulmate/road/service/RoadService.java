package com.ten.soulmate.road.service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ten.soulmate.global.dto.ResponseDto;
import com.ten.soulmate.member.repository.MemberRepository;
import com.ten.soulmate.road.dto.CheckCalendarRoadDto;
import com.ten.soulmate.road.dto.CheckCalendarRoadResponseDto;
import com.ten.soulmate.road.dto.GetRoadDetailResponseDto;
import com.ten.soulmate.road.dto.GetRoadDto;
import com.ten.soulmate.road.dto.GetRoadResponseDto;
import com.ten.soulmate.road.dto.RemindResponseDto;
import com.ten.soulmate.road.dto.RoadCountResponseDto;
import com.ten.soulmate.road.dto.RoadData;
import com.ten.soulmate.road.dto.SaveRoadDto;
import com.ten.soulmate.road.entity.Road;
import com.ten.soulmate.road.repository.RoadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoadService {
	
	private final RoadRepository roadRepository;
	private final MemberRepository memberRepository;
	
	public ResponseEntity<?> getRoadList(GetRoadDto request)
	{
		try {  	        			
			List<Road> roadList = roadRepository.findRoadList(request.getMemberId(), request.getSelectDate());						
			GetRoadResponseDto response = new GetRoadResponseDto();
			List<RoadData> roadDataList = new ArrayList<RoadData>();			
			
			for(Road road : roadList)
			{
				String roadStatus = "";
				
				if(road.getRoadStatus() == 0)
					roadStatus = "미선택";
				if(road.getRoadStatus() == 1)
					roadStatus = "선택완료";
				if(road.getRoadStatus() == 2)
					roadStatus = "회고완료";
				
				RoadData data = RoadData.builder()
								.id(road.getId())
								.summary(road.getSummary())
								.roadStatus(roadStatus)
								.title(road.getTitle())
								.createAt(road.getCreateAt())
								.build();
				roadDataList.add(data);				
			}
			
			response.setRoadList(roadDataList);
			log.info("Get RoadList Success!");
			
			return ResponseEntity.ok(response);		
		}
		catch(Exception e)
		{
			ResponseDto res = new ResponseDto();
			res.setMessage("Failed");			
			log.error("GetRoadList Error : "+e.getMessage());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
		}
				
	}
	
	
	public ResponseEntity<?> CheckCalendarRoadDay(CheckCalendarRoadDto request)
	{		
		try {			
			YearMonth ym = YearMonth.parse(request.getSelectMonth());
			List<Integer> dayList = roadRepository.findExistRoadDay(ym.getYear() , ym.getMonth().getValue(), request.getMemberId());
			CheckCalendarRoadResponseDto response = new CheckCalendarRoadResponseDto();			
			response.setExistsRoadDay(dayList);		
			log.info("Check Calendar Road Day Success!");
								
			return ResponseEntity.ok(response);	
		} catch(Exception e)
		{
			ResponseDto res = new ResponseDto();
			res.setMessage("Failed");			
			log.error("GetRoadList Error : "+e.getMessage());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
		}		
	}
	
	public ResponseEntity<?> countRoad(Long memberId)
	{
		try {			
			RoadCountResponseDto response = new RoadCountResponseDto();
			response.setRoadCount(roadRepository.countByMemberId(memberId));			
			
			log.info("Count Road Success!");
			
			return ResponseEntity.ok(response);				
		} catch(Exception e)
		{
			ResponseDto res = new ResponseDto();
			res.setMessage("Failed");
			
			log.error("Count Road Error : "+e.getMessage());
			
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
		}
		
	}
	
	public ResponseEntity<?> getRoadDetail(Long roadId)
	{
		try {
			Optional<Road> road = roadRepository.findById(roadId);
						
			GetRoadDetailResponseDto response = GetRoadDetailResponseDto.builder()
												//.conclusionTitle("소울메이트는 "+road.get().getRecommend()+"안을 추천합니다.")
												.conclusionTitle(road.get().getTitleConclusion())
												.thinkingContent(road.get().getThinkinContent())
												.conclusion(road.get().getConclusion())
												.titleA(road.get().getTitleA())
												.contentA(road.get().getAnswerA())
												.titleB(road.get().getTitleB())
												.contentB(road.get().getAnswerB())
												.result(road.get().getResult())
												.review(road.get().getReview())
												.build();
			return ResponseEntity.ok(response);							
		} catch(Exception e)
		{
			ResponseDto res = new ResponseDto();
			res.setMessage("Failed");
			log.error("Get Road Detail Error : "+e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
		}
	}
	
	@Transactional
	public ResponseEntity<?> saveRoad(SaveRoadDto request)
	{
		ResponseDto res = new ResponseDto();
		try {
			
			Optional<Road> road = roadRepository.findById(request.getId());		
			//선택만 했을 경우 상태를 1로 갱신
			if (request.getResult() != null && !request.getResult().isBlank()) {
			    
			    // 선택만 했을 경우 (회고 없음)
			    if (request.getReview() == null || request.getReview().isBlank()) {
			        road.get().updateResult(request.getResult());
			    }

			    // 선택 + 회고 모두 작성했을 경우
			    else {
			        road.get().updateResultAndReview(request.getResult(), request.getReview());
			    }
			}
			res.setMessage("Success");
			return ResponseEntity.ok(res);
			
		} catch(Exception e)
		{
			res.setMessage("Failed");
			log.error("Save Road : "+e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
		}
						
	}
		
	public ResponseEntity<?> getRemindRoad(Long memberId)
	{
		try {
			Optional<Road> road = roadRepository.findNotSelectRoad(memberId);
			RemindResponseDto response = null;
						
			if(road.isPresent())
			{
				
				LocalDateTime roadCreateTime = road.get().getCreateAt();
				LocalDateTime now = LocalDateTime.now();
				// 시간 차이 계산 (단위: 시간)
				long hours = ChronoUnit.HOURS.between(roadCreateTime, now);				
				long day = hours / 24;
								
				String category = road.get().getCategory();
				String memberName = memberRepository.findById(memberId).get().getName();
				category = getPostWord(category, "을", "를");
				
				
				response = RemindResponseDto.builder()
							.title(memberName+"님 "+day+"일 전에 "+category+" 고민했어요.")
							.roadId(road.get().getId())
							.remindYn("Y")
							.build();							
			}
			else {
				response = RemindResponseDto.builder()
							.remindYn("N").build();
			}								 										

			return ResponseEntity.ok(response);							
		} catch(Exception e)
		{
			ResponseDto res = new ResponseDto();
			res.setMessage("Failed");
			log.error("Get Not Select Road Error : "+e.getMessage());
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
		}
	}
	
	private String getPostWord(String str, String firstVal, String secondVal) {

		try {
				char laststr = str.charAt(str.length() - 1);
				// 한글의 제일 처음과 끝의 범위밖일 경우는 오류
				if (laststr < 0xAC00 || laststr > 0xD7A3) {
				    return str;
				}
		
				int lastCharIndex = (laststr - 0xAC00) % 28;
		
				// 종성인덱스가 0이상일 경우는 받침이 있는경우이며 그렇지 않은경우는 받침이 없는 경우
				if(lastCharIndex > 0) {
					// 받침이 있는경우
					// 조사가 '로'인경우 'ㄹ'받침으로 끝나는 경우는 '로' 나머지 경우는 '으로'
					if(firstVal.equals("으로") && lastCharIndex == 8) {
						str += secondVal;
					} else {
						str += firstVal;
					}
				} else {
					// 받침이 없는 경우
					str += secondVal;
				}
			} catch (Exception e) {
				//e.printStackTrace();
			}

			return str;
		}
	
}
