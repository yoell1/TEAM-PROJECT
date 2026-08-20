package com.fixer.verify.model.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class FixerLicenseDTO {

	private Long          licenseId;
	private String        fixerId;
	private String        licenseName;   // NOT NULL
	private String        licenseNo;
	private String        licenseFile;   // NOT NULL — 서버에 저장된 경로
	private LocalDate     issuedAt;      // DATE     → LocalDate
	private LocalDateTime uploadedAt;    // TIMESTAMP → LocalDateTime
}