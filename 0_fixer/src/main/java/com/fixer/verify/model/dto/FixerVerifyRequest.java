package com.fixer.verify.model.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 화면(verify.jsp) → 서버 로 넘어오는 값을 통째로 받는 그릇.
 * 대응하는 테이블이 없다. 서비스가 이걸 4개 테이블로 쪼개서 나눠 담는다.
 */
@Getter @Setter @NoArgsConstructor @ToString
public class FixerVerifyRequest {

	// → FIXER_PROFILE
	private String fixerIntro;
	private String fixerCareer;

	// → FIXER_REGION   (체크박스 여러 개)
	private List<String> regionNames;

	// → FIXER_CATEGORY (체크박스 여러 개)
	private List<String> categoryIds;

	// → FIXER_LICENSE  (아래 4개가 같은 index 끼리 한 세트)
	private List<String>        licenseNames;
	private List<String>        licenseNos;
	private List<String>        licenseIssuedAts;  // "2023-05-10" 문자열로 받음
	private List<MultipartFile> licenseFiles;
}
/*
licenseIssuedAts 를 LocalDate 가 아니라 String 으로 받는 이유: 
<input type="date"> 를 비워두면 빈 문자열 "" 이 날아오는데,
 스프링이 그걸 LocalDate 로 바꾸려다 바인딩 에러를 내.
 문자열로 받아서 서비스에서 "비었으면 null" 로 직접 처리하는 게 안전해.*/