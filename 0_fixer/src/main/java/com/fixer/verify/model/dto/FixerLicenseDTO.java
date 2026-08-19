package com.fixer.verify.model.dto;

import java.time.LocalDate;
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
public class FixerLicenseDTO {

	/*
LICENSE_ID	NUMBER		
FIXER_ID	VARCHAR2(30)		
LICENSE_NAME	VARCHAR2(100)		
LICENSE_NO	VARCHAR2(100)		
LICENSE_FILE	VARCHAR2(500)		
ISSUED_AT	DATE		
UPLOADED_AT	TIMESTAMP
	 */
	private Long licenseId;
	private String fixerId;
	private String licenseName;
	private String licenseNo;
	private String licenseFile;
	private LocalDate issuedAt;
	private LocalDateTime uploadedAt;
}