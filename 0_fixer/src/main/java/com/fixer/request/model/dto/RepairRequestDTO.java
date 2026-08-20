package com.fixer.request.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class RepairRequestDTO {

	private Long    repairNo;
	private String  receiptCode;      // R-260820-001
	private String  receiptTitle;
	private String  modelName;
	private String  receiptDetails;   // 상세 화면에서만 사용
	private String  receiptAddress;
	private String  receiptStatus;
	private Integer receiptUrgent;    // 1 이면 긴급

	private String  categoryId;
	private String  categoryItem;     // CATEGORY 조인
	private String  userName;         // USERS 조인 (고객 이름)

	// 화면에 그대로 뿌릴 문자열. SQL 의 TO_CHAR 로 미리 만들어서 받는다
	private String  visitAtText;      // 2026-08-25 14:00
	private String  createdAtText;    // 2026-08-20 09:12

	private Long    myEstimateId;     // 내가 이미 낸 견적 (없으면 null)
	private Integer photoCount;
}
/*
 * 날짜를 TO_CHAR 로 받는 이유: JSTL 의 <fmt:formatDate> 는 옛날 java.util.Date 용이라 LocalDateTime 을 못 받아.
 *  JSP에서 날짜 포맷 맞추려면 방법이 번거로워져. 
 *  SQL에서 이미 문자로 만들어 보내면 JSP는 그냥 ${r.visitAtText} 로 찍기만 하면 돼.
 */
