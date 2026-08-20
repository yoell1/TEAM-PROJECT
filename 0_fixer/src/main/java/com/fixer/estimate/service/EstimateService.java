package com.fixer.estimate.service;

import java.util.List;

import com.fixer.estimate.model.dto.EstimateDTO;
import com.fixer.estimate.model.dto.EstimateForm;

public interface EstimateService {

	/** 견적 작성 화면에 필요한 기존 견적 (없으면 null) */
	EstimateDTO getMyEstimate(Long repairNo, String fixerId);

	/** 내 견적 목록 */
	List<EstimateDTO> getMyEstimates(String fixerId);

	/** 견적 제시 (없으면 등록, 있으면 수정) */
	void submit(String fixerId, EstimateForm form);

	/** 견적 철회 */
	void withdraw(String fixerId, Long estimatesId);
}