package com.fixer.request.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixer.common.FixerGuard;
import com.fixer.request.model.dto.RepairRequestDTO;
import com.fixer.request.model.mapper.RequestMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

	private final RequestMapper mapper;
	private final FixerGuard    guard;


	@Override
	@Transactional(readOnly = true)
	public List<RepairRequestDTO> getNearbyRequests(String fixerId) {

		guard.requireApprovedFixer(fixerId);
		return mapper.selectNearbyRequests(fixerId);
	}


	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> getRequestDetail(Long repairNo, String fixerId) {

		guard.requireApprovedFixer(fixerId);

		RepairRequestDTO request = mapper.selectRequestDetail(repairNo, fixerId);

		if (request == null) {
			// 없는 번호이거나, 내 지역·분야 밖이거나, 이미 마감된 건
			throw new IllegalStateException("조회할 수 없는 접수입니다.");
		}

		Map<String, Object> result = new HashMap<>();
		result.put("request", request);
		result.put("photos", mapper.selectRequestPhotos(repairNo));

		return result;
	}
}

/*
@Transactional(readOnly = true) — 조회만 하는 메서드에 붙여. 
DB에게 "나 안 고칠 거야" 라고 미리 알려주는 거라 최적화가 되고, 
실수로 INSERT 를 넣었을 때 막아주기도 해.

requireApprovedFixer 가 F-14 와 F-15 를 잇는 고리야. 
인증 안 받은 사람이 접수를 보면 안 되잖아.
 그리고 이 검사는 F-16, F-17 에서도 똑같이 필요해.
 나중에 공통으로 뺄 자리야.*/
