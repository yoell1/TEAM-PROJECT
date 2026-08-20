package com.fixer.job.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class JobDTO {

	private Long    repairNo;
	private String  receiptCode;
	private String  receiptTitle;
	private String  modelName;
	private String  receiptDetails;
	private String  receiptAddress;
	private String  receiptStatus;
	private Integer receiptUrgent;

	private String  categoryItem;
	private String  userName;
	private String  userPnumber;      // 방문해야 하니 연락처 필요

	private String  visitAtText;
	private String  visitConfirmedAtText;
	private String  canceledAtText;
	private String  cancelReason;

	private Long    myPrice;
	private String  myDuration;
	private String  myMessage;

	private String  statusLabel;      // 서비스가 채움
}