package com.fixer.estimate.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** 견적 작성 화면 → 서버 */
@Getter @Setter @NoArgsConstructor @ToString
public class EstimateForm {

	private Long   estimatesId;      // 신규일 땐 selectKey 가 채워줌
	private Long   repairNo;
	private String fixerId;
	private Long   estimatesPrice;
	private String estDuration;      // "약 30분"
	private String estMessage;

	private List<String> optionCodes; // 체크박스
}