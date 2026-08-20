package com.fixer.verify.service;

import java.io.IOException;
import java.util.List;

import com.fixer.verify.model.dto.CategoryDTO;
import com.fixer.verify.model.dto.FixerProfileDTO;
import com.fixer.verify.model.dto.FixerVerifyRequest;

public interface FixerService {

	/** 신청 화면의 '수리 분야' 체크박스 목록 */
	List<CategoryDTO> getCategoryList();

	/** 내 신청 상태 (없으면 null) */
	FixerProfileDTO getMyProfile(String fixerId);

	/** 기사 인증 신청 (신규 / 재신청) */
	void applyVerify(String fixerId, FixerVerifyRequest request) throws IOException;
}