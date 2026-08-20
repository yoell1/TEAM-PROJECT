package com.fixer.verify.service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fixer.common.util.FileUploadUtil;
import com.fixer.common.util.SavedFile;
import com.fixer.verify.model.dto.CategoryDTO;
import com.fixer.verify.model.dto.FixerLicenseDTO;
import com.fixer.verify.model.dto.FixerProfileDTO;
import com.fixer.verify.model.dto.FixerVerifyRequest;
import com.fixer.verify.model.mapper.FixerMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FixerServiceImpl implements FixerService {

	// FIXER_APPROVAL 에 CHECK 제약이 없으므로 자바 쪽에서 오타를 막는다
	public static final String PENDING  = "PENDING";
	public static final String APPROVED = "APPROVED";
	public static final String REJECTED = "REJECTED";

	private final FixerMapper mapper;
	private final FileUploadUtil fileUploadUtil;

	@Value("${file.upload-dir.license}")
	private String licenseUploadDir;

	@Value("${file.web-prefix.license}")
	private String licenseWebPrefix;


	@Override
	public List<CategoryDTO> getCategoryList() {
		return mapper.selectCategoryList();
	}

	@Override
	public FixerProfileDTO getMyProfile(String fixerId) {
		return mapper.selectFixerProfile(fixerId);
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void applyVerify(String fixerId, FixerVerifyRequest request) throws IOException {

		// ---------- 0) 입력값 검증 ----------
		validate(request);

		// ---------- 1) 신규인가 재신청인가 ----------
		FixerProfileDTO profile = mapper.selectFixerProfile(fixerId);
		boolean isNew;

		if (profile == null) {
			isNew = true;
		} else if (PENDING.equals(profile.getFixerApproval())) {
			throw new IllegalStateException("이미 심사 중인 신청이 있습니다.");
		} else if (APPROVED.equals(profile.getFixerApproval())) {
			throw new IllegalStateException("이미 인증된 기사입니다.");
		} else {
			isNew = false;   // REJECTED → 재신청
		}

		// ---------- 2) 재신청이면 기존 데이터 정리 ----------
		// DB 행을 지우기 전에 파일 경로를 미리 확보해둔다.
		// 지우고 나면 어떤 파일을 삭제해야 할지 알 수 없게 되니까.
		List<FixerLicenseDTO> oldLicenses = new ArrayList<>();

		if (!isNew) {
			oldLicenses = mapper.selectLicensesByFixerId(fixerId);
			mapper.deleteLicensesByFixerId(fixerId);
			mapper.deleteRegionsByFixerId(fixerId);
			mapper.deleteCategoriesByFixerId(fixerId);
		}

		// ---------- 3) 프로필 저장 ----------
		FixerProfileDTO save = new FixerProfileDTO();
		save.setFixerId(fixerId);
		save.setFixerIntro(request.getFixerIntro());
		save.setFixerCareer(request.getFixerCareer());

		if (isNew) {
			mapper.insertFixerProfile(save);   // FIXER_NO 발급
		} else {
			mapper.updateFixerProfile(save);   // FIXER_NO 유지
		}

		// ---------- 4) 활동 지역 ----------
		for (String regionName : request.getRegionNames()) {
			mapper.insertFixerRegion(fixerId, regionName);
		}

		// ---------- 5) 수리 분야 ----------
		for (String categoryId : request.getCategoryIds()) {
			mapper.insertFixerCategory(fixerId, categoryId);
		}

		// ---------- 6) 자격증 ----------
		int savedCount = saveLicenses(fixerId, request);

		if (savedCount == 0) {
			// LICENSE_FILE 이 NOT NULL 이고, 증빙 없는 인증은 의미가 없다.
			// 여기서 던지면 @Transactional 이 위의 INSERT 들을 전부 되돌린다.
			throw new IllegalStateException("자격증 증빙파일을 최소 1개 올려주세요.");
		}

		// ---------- 7) 옛 파일 삭제 (반드시 맨 마지막) ----------
		// DB 작업이 전부 끝난 뒤에 지운다. 중간에 예외가 나면 DB는 롤백되지만
		// 이미 지워버린 파일은 되살릴 방법이 없기 때문.
		for (FixerLicenseDTO old : oldLicenses) {
			fileUploadUtil.delete(old.getLicenseFile(), licenseUploadDir);
		}
	}


	/** 자격증 목록을 저장하고, 실제로 저장된 건수를 돌려준다 */
	private int saveLicenses(String fixerId, FixerVerifyRequest request) throws IOException {

		List<MultipartFile> files = request.getLicenseFiles();
		List<String> names  = request.getLicenseNames();
		List<String> nos    = request.getLicenseNos();
		List<String> dates  = request.getLicenseIssuedAts();

		if (files == null) {
			return 0;
		}

		int count = 0;

		for (int i = 0; i < files.size(); i++) {

			SavedFile saved = fileUploadUtil.save(files.get(i), licenseUploadDir, licenseWebPrefix);

			if (saved == null) {
				continue;                      // 파일을 안 올린 칸
			}

			String name = pick(names, i);
			if (name == null || name.isBlank()) {
				throw new IllegalStateException((i + 1) + "번째 자격증의 자격증명을 입력해주세요.");
			}

			FixerLicenseDTO license = new FixerLicenseDTO();
			license.setFixerId(fixerId);
			license.setLicenseName(name.trim());
			license.setLicenseNo(blankToNull(pick(nos, i)));
			license.setLicenseFile(saved.getPath());
			license.setIssuedAt(toDate(pick(dates, i)));

			mapper.insertFixerLicense(license);
			count++;
		}

		return count;
	}


	// ---------- 작은 도우미들 ----------

	/** 리스트에서 i번째를 안전하게 꺼낸다 (없으면 null) */
	private String pick(List<String> list, int i) {
		return (list != null && i < list.size()) ? list.get(i) : null;
	}

	private String blankToNull(String s) {
		return (s == null || s.isBlank()) ? null : s.trim();
	}

	/** "2023-05-10" → LocalDate, 비었으면 null */
	private LocalDate toDate(String s) {
		if (s == null || s.isBlank()) {
			return null;
		}
		try {
			return LocalDate.parse(s.trim());
		} catch (Exception e) {
			throw new IllegalStateException("발급일 형식이 올바르지 않습니다: " + s);
		}
	}

	private void validate(FixerVerifyRequest r) {

		if (r.getRegionNames() == null || r.getRegionNames().isEmpty()) {
			throw new IllegalStateException("활동 지역을 최소 1개 선택해주세요.");
		}
		if (r.getCategoryIds() == null || r.getCategoryIds().isEmpty()) {
			throw new IllegalStateException("수리 가능 분야를 최소 1개 선택해주세요.");
		}
		// DB 컬럼 길이를 넘으면 ORA-12899 라는 알아보기 힘든 에러가 난다.
		// 사용자에게는 읽을 수 있는 말로 먼저 알려준다.
		if (r.getFixerIntro() != null && r.getFixerIntro().length() > 500) {
			throw new IllegalStateException("자기소개는 500자를 넘을 수 없습니다.");
		}
		if (r.getFixerCareer() != null && r.getFixerCareer().length() > 200) {
			throw new IllegalStateException("경력은 200자를 넘을 수 없습니다.");
		}
	}
}

/*
 * 여기가 원래 코드에서 가장 크게 고친 부분이야. 
 * 이전 버전은 "옛 파일 삭제"가 자격증 for 루프 안에 들어가 있었어. 
 * 그러면 자격증을 3개 올릴 때 같은 파일을 3번 지우려 들고, 
 * 반대로 파일을 하나도 안 올리면 루프에 아예 안 들어가서 옛 파일이 영원히 안 지워져. 
 * 지금은 루프 밖 맨 마지막으로 뺐어.
 */
