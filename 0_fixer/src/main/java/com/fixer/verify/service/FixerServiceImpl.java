package com.fixer.verify.service;

import java.io.IOException;
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

@Service                    // 처리 규칙(비즈니스 로직) 담당
@RequiredArgsConstructor    // final 필드를 받는 생성자 자동 생성 → 의존성 주입
public class FixerServiceImpl implements FixerService {

	private final FixerMapper mapper;
	private final FileUploadUtil fileUploadUtil;

	// application.properties 의 file.upload-dir.license 값 (uploads/license)
	@Value("${file.upload-dir.license}")
	private String licenseUploadDir;


	@Override
	public List<CategoryDTO> getCategoryList() {
		return mapper.selectCategoryList();
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void applyVerify(String userId, FixerVerifyRequest request)
			throws IllegalStateException, IOException {

		// ---------- 1) 현재 신청 상태 확인 ----------
		FixerProfileDTO profile = mapper.selectFixerProfile(userId);

		boolean isNew;

		if (profile == null) {
			// 신청 이력 없음 → 신규 신청
			isNew = true;

		} else if ("PENDING".equals(profile.getFixerApproval())) {
			throw new IllegalStateException("이미 심사 중인 신청이 있습니다.");

		} else if ("APPROVED".equals(profile.getFixerApproval())) {
			throw new IllegalStateException("이미 인증된 기사입니다.");

		} else {
			// REJECTED → 재신청
			isNew = false;
		}


		// ---------- 2) 재신청이면 기존 데이터 정리 ----------
		List<FixerLicenseDTO> oldLicenses = null;

		if (!isNew) {
			// 파일 경로를 미리 확보 (DB에서 지우면 경로를 알 수 없게 됨)
			oldLicenses = mapper.selectLicensesByFixerId(userId);

			mapper.deleteLicensesByFixerId(userId);
			mapper.deleteRegionsByFixerId(userId);
			mapper.deleteCategoriesByFixerId(userId);
		}

		// ---------- 3) 프로필 저장 ----------
		FixerProfileDTO saveProfile = new FixerProfileDTO(); // new? DTO는 빈(Bean)이 아니기 때문
		saveProfile.setUserId(userId);
		saveProfile.setFixerIntro(request.getFixerIntro());
		saveProfile.setFixerCareer(request.getFixerCareer());

		if (isNew) {
			mapper.insertFixerProfile(saveProfile);
		} else {
			mapper.updateFixerProfile(saveProfile);
		}


		// ---------- 4) 지역 / 분야 / 자격증 저장 ----------

		// 4-1) 활동 지역
		if (request.getRegionNames() != null) {
			for (String regionName : request.getRegionNames()) {
				mapper.insertFixerRegion(userId, regionName);
			}
		}

		// 4-2) 수리 분야
		if (request.getCategoryIds() != null) {
			for (String categoryId : request.getCategoryIds()) {
				mapper.insertFixerCategory(userId, categoryId);
			}
		}

		// 4-3) 자격증 (이름 · 번호 · 파일이 같은 순서로 짝을 이룸)
		List<MultipartFile> files = request.getLicenseFiles();
		List<String> names = request.getLicenseNames();
		List<String> nos = request.getLicenseNos();

		if (files != null) {
			for (int i = 0; i < files.size(); i++) {

				// 서버에 파일 저장
				SavedFile saved = fileUploadUtil.save(
						files.get(i), licenseUploadDir, "/uploads/license");

				// 파일을 안 올린 칸은 건너뜀
				if (saved == null) {
					continue;
				}

				String name = (names != null && i < names.size()) ? names.get(i) : null;
				String no   = (nos   != null && i < nos.size())   ? nos.get(i)   : null;

				if (name == null || name.isBlank()) {
					throw new IllegalStateException("자격증명을 입력해주세요.");
				}

				FixerLicenseDTO license = new FixerLicenseDTO();
				license.setFixerId(userId);
				license.setLicenseName(name);
				license.setLicenseNo(no);
				license.setLicenseFile(saved.getPath());

				mapper.insertFixerLicense(license);
				
				
				// ---------- 5) 옛 파일 삭제 (모든 DB 작업이 끝난 뒤) ----------
				if (oldLicenses != null) {
					for (FixerLicenseDTO old : oldLicenses) {
						fileUploadUtil.delete(old.getLicenseFile(), licenseUploadDir);
					}
				}
			}
		}
	}
}