package com.fixer.verify.model.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FixerProfileDTO {
	/*
USER_ID	VARCHAR2(30)		
FIXER_NO	NUMBER		
FIXER_APPROVAL	VARCHAR2(20)   
FIXER_APPROVED_AT	TIMESTAMP		
FIXER_REJECT_REASON	VARCHAR2(500)		
FIXER_INTRO	VARCHAR2(500)		
FIXER_CAREER	VARCHAR2(200)		
	 */
	private String userId;
	private int fixerNo;
	private String fixerApproval;
	private LocalDateTime fixerApprovedAt;
	private String fixerRejectReason;
	private String fixerIntro;
	private String fixerCareer;

}
