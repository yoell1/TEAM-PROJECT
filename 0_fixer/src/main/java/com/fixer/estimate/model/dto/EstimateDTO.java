package com.fixer.estimate.model.dto;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/** DB → 화면 (접수 정보까지 조인해서 담는다) */
@Getter @Setter @NoArgsConstructor @ToString
public class EstimateDTO {

	private Long   estimatesId;
	private Long   repairNo;
	private String fixerId;
	private Long   estimatesPrice;
	private String estimatesStatus;   // PROPOSED / ACCEPTED / REJECTED / WITHDRAWN
	private String estDuration;
	private String estMessage;
	private String proposedAtText;
	private String acceptedAtText;

	// 조인해서 가져오는 접수 정보
	private String receiptCode;
	private String receiptTitle;
	private String categoryItem;
	private String userName;
	private String receiptStatus;

	// 옵션 (서비스가 채움)
	private List<String> optionCodes;
	private List<String> optionLabels;
}