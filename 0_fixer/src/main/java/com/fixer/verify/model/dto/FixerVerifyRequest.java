package com.fixer.verify.model.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class FixerVerifyRequest {

	// 프로필 정보 → FIXER_PROFILE 테이블로
	private String fixerIntro;
	private String fixerCareer;

	// 활동 지역 (여러 개) → FIXER_REGION 테이블로
	private List<String> regionNames;

	// 수리 가능 분야 (여러 개) → FIXER_CATEGORY 테이블로
	private List<String> categoryIds;

	// 자격증 (여러 개) → FIXER_LICENSE 테이블로
	private List<String> licenseNames;
	private List<String> licenseNos;
	private List<MultipartFile> licenseFiles;
}
/*
 *         FixerVerifyRequest (받는 그릇)
                 │
     ┌───────────┼───────────┬────────────┐
     ▼           ▼           ▼            ▼
FIXER_PROFILE  REGION    CATEGORY     LICENSE
컨트롤러가 이걸로 통째로 받고, 서비스가 4개로 쪼개서 각 테이블에 나눠 담습니다.

List<String> 이 왜 필요한가

체크박스는 여러 개를 고를 수 있죠.

html
☑ 서울시 강남구
☑ 서울시 서초구
☐ 서울시 송파구

이렇게 2개를 고르면 서버로 값이 2개 날아옵니다. String 하나로는 못 받아요.

java
private String regionName;         // ❌ 하나만 담김
private List<String> regionNames;  // ✅ 여러 개 담김

List<String> = "문자열 여러 개를 담는 목록". 스프링이 알아서 체크된 값들을 여기 채워줍니다.
필드 이름 = 화면의 name 속성이어야 자동으로 연결됩니다. 
JSP에서 <input name="regionNames"> 라고 써야 이 필드에 담겨요

MultipartFile 은 스프링이 제공하는 "업로드된 파일" 전용 타입입니다. 
이 안에 원본 파일명, 크기, 실제 데이터가 다 들어 있어요.

java
file.getOriginalFilename()   // "자격증.png"
file.getSize()               // 파일 크기
file.isEmpty()               // 비었는지
file.transferTo(저장할위치)   // 서버에 저장!

자격증도 여러 개 올릴 수 있으니 List<MultipartFile> 입니다.

아까 application.properties에 적은 이 설정이 여기서 쓰입니다.

자격증 3개 필드가 짝을 이룹니다
java
	private List<String> licenseNames;   // ["전기기능사", "정보처리기사"]
	private List<String> licenseNos;     // ["12345",     "67890"]
	private List<MultipartFile> licenseFiles;  // [파일1,   파일2]

같은 순서(index)끼리 한 세트입니다. 0번끼리, 1번끼리 묶어서 FIXER_LICENSE 한 행씩 만듬


@AllArgsConstructor 는 왜 뺐나

이 클래스는 스프링이 자동으로 채워주는 용도입니다. 우리가 new 로 만들 일이 없어요.

스프링이 값을 채우는 방식이 딱 이겁니다:

1. new FixerVerifyRequest()      ← @NoArgsConstructor 필요
2. setFixerIntro("...")          ← @Setter 필요
3. setRegionNames([...])         ← @Setter 필요
   ...

빈 생성자 + setter 만 있으면 되고, 전체 생성자는 쓸 일이 없어서 뺐습니다.

@ToString 은 남겨뒀는데, "폼에서 값이 제대로 넘어왔나?" 를 확인할 때 
System.out.println(request) 한 줄로 전부 볼 수 있어서 아주 유용하거든요.

DTO	성격
FixerProfileDTO	테이블 ↔ 자바
FixerLicenseDTO	테이블 ↔ 자바
CategoryDTO	테이블 ↔ 자바
FixerVerifyRequest	화면 ↔ 자바 (테이블 없음)
*/
