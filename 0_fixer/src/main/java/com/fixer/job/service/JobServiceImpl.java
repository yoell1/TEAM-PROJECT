package com.fixer.job.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fixer.job.model.dto.JobDTO;
import com.fixer.job.model.dto.JobStatus;
import com.fixer.job.model.mapper.JobMapper;
import com.fixer.request.model.mapper.RequestMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JobServiceImpl implements JobService {

	private static final String APPROVED = "APPROVED";

	private final JobMapper     mapper;
	private final RequestMapper requestMapper;


	@Override
	@Transactional(readOnly = true)
	public List<JobDTO> getMyJobs(String fixerId) {

		requireApprovedFixer(fixerId);

		List<JobDTO> jobs = mapper.selectMyJobs(fixerId);
		for (JobDTO job : jobs) {
			job.setStatusLabel(JobStatus.labelOf(job.getReceiptStatus()));
		}
		return jobs;
	}


	@Override
	@Transactional(readOnly = true)
	public JobDTO getMyJob(Long repairNo, String fixerId) {

		requireApprovedFixer(fixerId);

		JobDTO job = mapper.selectMyJob(repairNo, fixerId);
		if (job == null) {
			throw new IllegalStateException("조회할 수 없는 작업입니다.");
		}
		job.setStatusLabel(JobStatus.labelOf(job.getReceiptStatus()));

		return job;
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void moveStatus(String fixerId, Long repairNo, String toStatus) {

		requireApprovedFixer(fixerId);

		JobDTO job = mapper.selectMyJob(repairNo, fixerId);
		if (job == null) {
			throw new IllegalStateException("내 작업이 아닙니다.");
		}

		String from = job.getReceiptStatus();

		if (!JobStatus.canMove(from, toStatus)) {
			throw new IllegalStateException(
					JobStatus.labelOf(from) + " 상태에서는 "
					+ JobStatus.labelOf(toStatus) + " 로 바꿀 수 없습니다.");
		}

		int updated = mapper.updateJobStatus(repairNo, fixerId, from, toStatus);

		if (updated == 0) {
			// 조회한 뒤 UPDATE 하기 전에 다른 곳에서 상태가 바뀐 경우
			throw new IllegalStateException("상태가 이미 변경되었습니다. 새로고침 후 다시 시도해주세요.");
		}
	}


	@Override
	@Transactional(rollbackFor = Exception.class)
	public void cancel(String fixerId, Long repairNo, String reason) {

		requireApprovedFixer(fixerId);

		if (reason == null || reason.isBlank()) {
			throw new IllegalStateException("취소 사유를 입력해주세요.");
		}
		if (reason.length() > 500) {
			throw new IllegalStateException("취소 사유는 500자를 넘을 수 없습니다.");
		}

		int updated = mapper.cancelJob(repairNo, fixerId, reason.trim());

		if (updated == 0) {
			throw new IllegalStateException("취소할 수 없는 작업입니다. (이미 완료되었거나 내 작업이 아님)");
		}
	}


	private void requireApprovedFixer(String fixerId) {

		String approval = requestMapper.selectFixerApproval(fixerId);

		if (approval == null) {
			throw new IllegalStateException("기사 인증 신청을 먼저 해주세요.");
		}
		if (!APPROVED.equals(approval)) {
			throw new IllegalStateException("기사 인증이 완료된 후 이용할 수 있습니다.");
		}
	}
}
/*
 * 검사를 두 번 하는 게 이상해 보일 거야 — canMove 로 자바에서 한 번, SQL 의 WHERE ... = #{fromStatus} 로 또 한 번.
이유가 있어. 자바 검사는 사람이 읽을 수 있는 에러 메시지를 만들기 위한 거고 ("완료 상태에서는 수리중으로 바꿀 수 없습니다"),
 SQL 검사는 조회와 수정 사이의 틈을 막기 위한 거야.
 그 사이에 고객이 취소해버리면 자바 검사는 통과했는데 실제로는 안 바뀌어야 하거든.
 updated == 0 이 그걸 잡아줘.
 */
