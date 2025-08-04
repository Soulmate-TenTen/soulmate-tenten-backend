package com.ten.soulmate.road.dto;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetRoadDto {

	 private Long memberId;
	 
	 @DateTimeFormat(pattern = "yyyy-MM-dd")
	 private LocalDate selectDate;

}
