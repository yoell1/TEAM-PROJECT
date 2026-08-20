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
			throw new IllegalStateException("자격증 증빙파일을 최소 1개 올려주세요.");
		}

		// ---------- 7) 옛 파일 삭제 (반드시 맨 마지막) ----------
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

		// 이번 요청에서 디스크에 만든 파일들. 실패하면 이걸 보고 치운다
		List<String> savedPaths = new ArrayList<>();

		try {
			int count = 0;

			for (int i = 0; i < files.size(); i++) {

				MultipartFile file = files.get(i);

				// 안 올린 칸은 건너뜀. 디스크는 건드리지 않는다
				if (file == null || file.isEmpty()) {
					continue;
				}

				// ---- 검증을 파일 저장보다 먼저 끝낸다 ----
				String name = pick(names, i);
				if (name == null || name.isBlank()) {
					throw new IllegalStateException((i + 1) + "번째 자격증의 자격증명을 입력해주세요.");
				}

				LocalDate issuedAt = toDate(pick(dates, i));

				// ---- 검증 통과 후 저장 ----
				SavedFile saved = fileUploadUtil.save(file, licenseUploadDir, licenseWebPrefix);
				savedPaths.add(saved.getPath());

				FixerLicenseDTO license = new FixerLicenseDTO();
				license.setFixerId(fixerId);
				license.setLicenseName(name.trim());
				license.setLicenseNo(blankToNull(pick(nos, i)));
				license.setLicenseFile(saved.getPath());
				license.setIssuedAt(issuedAt);

				mapper.insertFixerLicense(license);
				count++;
			}

			return count;

		} catch (RuntimeException | IOException e) {
			// @Transactional 이 DB 는 되돌려주지만 파일은 안 되돌린다.
			for (String path : savedPaths) {
				fileUploadUtil.delete(path, licenseUploadDir);
			}
			throw e;
		}
	}


	// ---------- 작은 도우미들 ----------

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
		if (r.getFixerIntro() != null && r.getFixerIntro().length() > 500) {
			throw new IllegalStateException("자기소개는 500자를 넘을 수 없습니다.");
		}
		if (r.getFixerCareer() != null && r.getFixerCareer().length() > 200) {
			throw new IllegalStateException("경력은 200자를 넘을 수 없습니다.");
		}
	}
}
