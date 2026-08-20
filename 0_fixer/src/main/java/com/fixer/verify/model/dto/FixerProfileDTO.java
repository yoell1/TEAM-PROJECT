package com.fixer.verify.model.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class FixerProfileDTO {

	private String        fixerId;            // ★ USER_ID 아님. 팀 스키마는 FIXER_ID
	private Integer       fixerNo;            // SEQ_FIXER_NO 로 발급, UNIQUE
	private String        fixerApproval;      // PENDING / APPROVED / REJECTED
	private LocalDateTime fixerApprovedAt;
	private String        fixerRejectReason;  // 거절 사유 (재신청 화면에 보여줌)
	private String        fixerIntro;         // 500자
	private String        fixerCareer;        // 200자
}