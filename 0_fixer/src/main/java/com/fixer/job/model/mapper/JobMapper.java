package com.fixer.job.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fixer.job.model.dto.JobDTO;

@Mapper
public interface JobMapper {

	List<JobDTO> selectMyJobs(String fixerId);

	JobDTO selectMyJob(@Param("repairNo") Long repairNo,
	                   @Param("fixerId") String fixerId);

	int updateJobStatus(@Param("repairNo") Long repairNo,
	                    @Param("fixerId") String fixerId,
	                    @Param("fromStatus") String fromStatus,
	                    @Param("toStatus") String toStatus);

	int cancelJob(@Param("repairNo") Long repairNo,
	              @Param("fixerId") String fixerId,
	              @Param("reason") String reason);
}