package com.fixer.estimate.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fixer.estimate.model.dto.EstimateDTO;
import com.fixer.estimate.model.dto.EstimateForm;

@Mapper
public interface EstimateMapper {

	/** 이 접수에 내가 낸 견적 (없으면 null) */
	EstimateDTO selectMyEstimate(@Param("repairNo") Long repairNo,
	                             @Param("fixerId") String fixerId);

	/** 내 견적 전체 목록 */
	List<EstimateDTO> selectMyEstimates(String fixerId);

	/** 견적 1건 (본인 것만) */
	EstimateDTO selectMyEstimateById(@Param("estimatesId") Long estimatesId,
	                                 @Param("fixerId") String fixerId);

	List<String> selectOptionCodes(Long estimatesId);

	int insertEstimate(EstimateForm form);
	int updateEstimate(EstimateForm form);

	int deleteOptions(Long estimatesId);
	int insertOption(@Param("estimatesId") Long estimatesId,
	                 @Param("optionCode") String optionCode);

	/** PROPOSED 인 내 견적만 철회 가능 */
	int withdrawEstimate(@Param("estimatesId") Long estimatesId,
	                     @Param("fixerId") String fixerId);
}
/*
 * <selectKey order="BEFORE"> 가 이번의 핵심이야. 
 * 견적을 넣고 그 견적의 ID로 옵션을 넣어야 하는데, 
 * SEQ.NEXTVAL 을 VALUES 안에서만 쓰면 자바는 그 번호를 몰라.
 *  selectKey 는 INSERT 전에 시퀀스를 먼저 뽑아서 DTO 필드에 채워줘.
 *  F-14 에선 자식(자격증)이 부모 ID(FIXER_ID)를 이미 알고 있어서 필요 없었지만, 
 *  여기선 부모 ID가 새로 생기니까 필요해.

   withdrawEstimate 의 WHERE 절도 눈여겨봐.
    FIXER_ID 와 ESTIMATES_STATUS 조건을 SQL 안에 넣었잖아.
     자바에서 조회→검사→수정 3단계로 하면 그 사이에 값이 바뀔 수 있어.
      한 문장으로 처리하고 "몇 건 바뀌었나"로 판단하는 게 안전해.
 */
