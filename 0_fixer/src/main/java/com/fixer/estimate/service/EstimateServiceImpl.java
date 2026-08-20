package com.fixer.estimate.service;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixer.common.FixerGuard;
import com.fixer.estimate.model.dto.EstimateDTO;
import com.fixer.estimate.model.dto.EstimateForm;
import com.fixer.estimate.model.dto.EstimateOption;
import com.fixer.estimate.model.mapper.EstimateMapper;
import com.fixer.request.model.dto.RepairRequestDTO;
import com.fixer.request.model.mapper.RequestMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EstimateServiceImpl implements EstimateService {

	private static final String PROPOSED  = "PROPOSED";
	private static final long   MAX_PRICE = 10_000_000L;

	private final EstimateMapper mapper;
	private final RequestMapper  requestMapper;   // 접수 열람 자격 검사를 F-15 것과 공유
	private final FixerGuard     guard;


	@Override
	@Transactional(readOnly = true)
	public EstimateDTO getMyEstimate(Long repairNo, String fixerId) {

		guard.requireApprovedFixer(fixerId);

		EstimateDTO estimate = mapper.selectMyEstimate(repairNo, fixerId);
		fillOptions(estimate);

		return estimate;
	}


	@Override
	@Transactional(readOnly = true)
	public List<EstimateDTO> getMyEstimates(String fixerId) {

		guard.requireApprovedFixer(fixerId);

		List<EstimateDTO> list = mapper.selectMyEstimates(fixerId);
		for (EstimateDTO e : list) {
			fillOptions(e);
		}
		return list;
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void submit(String fixerId, EstimateForm form) {

		guard.requireApprovedFixer(fixerId);
		validate(form);

		// 내가 볼 수 있는(= 견적 낼 수 있는) 접수인지 F-15 조건 그대로 재사용
		RepairRequestDTO target = requestMapper.selectRequestDetail(form.getRepairNo(), fixerId);
		if (target == null) {
			throw new IllegalStateException("견적을 제시할 수 없는 접수입니다. (마감되었거나 내 담당이 아님)");
		}

		form.setFixerId(fixerId);

		EstimateDTO existing = mapper.selectMyEstimate(form.getRepairNo(), fixerId);

		if (existing == null) {
			// 신규 — selectKey 가 form.estimatesId 를 채워준다
			mapper.insertEstimate(form);

		} else {
			if (!PROPOSED.equals(existing.getEstimatesStatus())) {
				throw new IllegalStateException(
						"수정할 수 없는 견적입니다. (현재 상태: " + existing.getEstimatesStatus() + ")");
			}
			form.setEstimatesId(existing.getEstimatesId());
			mapper.updateEstimate(form);
			mapper.deleteOptions(form.getEstimatesId());
		}

		saveOptions(form.getEstimatesId(), form.getOptionCodes());
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void withdraw(String fixerId, Long estimatesId) {

		guard.requireApprovedFixer(fixerId);

		int updated = mapper.withdrawEstimate(estimatesId, fixerId);

		if (updated == 0) {
			// 남의 견적이거나, 이미 수락/거절된 견적
			throw new IllegalStateException("철회할 수 없는 견적입니다.");
		}
	}


	// ---------- 내부 도우미 ----------

	private void saveOptions(Long estimatesId, List<String> codes) {

		if (codes == null) {
			return;
		}
		// 중복 제거 + 순서 유지
		for (String code : new LinkedHashSet<>(codes)) {
			if (EstimateOption.isValid(code)) {
				mapper.insertOption(estimatesId, code);
			}
			// 목록에 없는 코드는 조용히 버린다 (화면 조작 방어)
		}
	}

	private void fillOptions(EstimateDTO estimate) {

		if (estimate == null) {
			return;
		}

		List<String> codes = mapper.selectOptionCodes(estimate.getEstimatesId());
		List<String> labels = new ArrayList<>();

		for (String code : codes) {
			labels.add(EstimateOption.labelOf(code));
		}

		estimate.setOptionCodes(codes);
		estimate.setOptionLabels(labels);
	}

	private void validate(EstimateForm form) {

		if (form.getRepairNo() == null) {
			throw new IllegalStateException("접수 정보가 없습니다.");
		}
		if (form.getEstimatesPrice() == null || form.getEstimatesPrice() <= 0) {
			throw new IllegalStateException("견적 금액을 올바르게 입력해주세요.");
		}
		if (form.getEstimatesPrice() > MAX_PRICE) {
			throw new IllegalStateException("견적 금액이 너무 큽니다. (최대 1,000만원)");
		}
		if (form.getEstDuration() != null && form.getEstDuration().length() > 50) {
			throw new IllegalStateException("예상 소요시간은 50자를 넘을 수 없습니다.");
		}
		if (form.getEstMessage() != null && form.getEstMessage().length() > 500) {
			throw new IllegalStateException("전달 메시지는 500자를 넘을 수 없습니다.");
		}
	}
}
