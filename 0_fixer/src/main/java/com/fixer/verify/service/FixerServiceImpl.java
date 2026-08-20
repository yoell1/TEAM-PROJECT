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
		int savedCount =
