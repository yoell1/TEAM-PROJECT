package com.fixer.request.service;

import java.util.List;
import java.util.Map;

import com.fixer.request.model.dto.RepairRequestDTO;

public interface RequestService {

	/** 내 주변 새 접수 목록 */
	List<RepairRequestDTO> getNearbyRequests(String fixerId);

	/** 접수 상세 + 사진 (키: request, photos) */
	Map<String, Object> getRequestDetail(Long repairNo, String fixerId);
}