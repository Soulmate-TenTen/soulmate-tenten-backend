package com.ten.soulmate.road.service;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.ten.soulmate.global.dto.ResponseDto;
import com.ten.soulmate.road.dto.CheckCalendarRoadDto;
import com.ten.soulmate.road.dto.CheckCalendarRoadResponseDto;
import com.ten.soulmate.road.dto.GetRoadDto;
import com.ten.soulmate.road.dto.GetRoadResponseDto;
import com.ten.soulmate.road.dto.RoadData;
import com.ten.soulmate.road.entity.Road;
import com.ten.soulmate.road.repository.RoadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoadService {
	
	private final RoadRepository roadRepository;
	
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
			List<Integer> dayList = roadRepository.findExistRoadDay(request.getSelectMonth().getYear() , request.getSelectMonth().getMonth().getValue(), request.getMemberId());
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
	
}
