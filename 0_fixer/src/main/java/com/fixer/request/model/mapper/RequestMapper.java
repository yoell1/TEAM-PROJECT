package com.fixer.request.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fixer.request.model.dto.RepairPhotoDTO;
import com.fixer.request.model.dto.RepairRequestDTO;

@Mapper
public interface RequestMapper {

	/** 기사의 승인 상태 (APPROVED 인지 확인용) */
	String selectFixerApproval(String fixerId);

	/** 내 지역 × 내 분야 에 맞는 대기중 접수 목록 */
	List<RepairRequestDTO> selectNearbyRequests(String fixerId);

	/** 접수 1건 상세 (내가 볼 자격이 있는 건만) */
	RepairRequestDTO selectRequestDetail(@Param("repairNo") Long repairNo,
	                                     @Param("fixerId") String fixerId);

	/** 접수 첨부 사진 */
	List<RepairPhotoDTO> selectRequestPhotos(Long repairNo);
}