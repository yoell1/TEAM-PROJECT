package com.fixer.verify.service;

import java.io.IOException;
import java.util.List;

import com.fixer.verify.model.dto.CategoryDTO;
import com.fixer.verify.model.dto.FixerVerifyRequest;

public interface FixerService {

	// 카테고리 전체 목록 조회
	List<CategoryDTO> getCategoryList();
	
	// 기사 인증 신청 (신규 / 재신청)
	void applyVerify(String userId, FixerVerifyRequest request)
                  //    ↑                    ↑
               //    누가 신청하나        무엇을 신청하나
			throws IllegalStateException, IOException;
}