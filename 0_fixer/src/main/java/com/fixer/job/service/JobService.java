package com.fixer.job.service;

import java.util.List;

import com.fixer.job.model.dto.JobDTO;

public interface JobService {

	List<JobDTO> getMyJobs(String fixerId);

	JobDTO getMyJob(Long repairNo, String fixerId);

	/** 상태 전이 (작업 시작 / 완료) */
	void moveStatus(String fixerId, Long repairNo, String toStatus);

	/** 작업 취소 */
	void cancel(String fixerId, Long repairNo, String reason);
}