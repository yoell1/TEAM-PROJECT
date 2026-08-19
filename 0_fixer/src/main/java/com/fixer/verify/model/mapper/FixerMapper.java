package com.fixer.verify.model.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.fixer.verify.model.dto.CategoryDTO;
import com.fixer.verify.model.dto.FixerLicenseDTO;
import com.fixer.verify.model.dto.FixerProfileDTO;

@Mapper
public interface FixerMapper { 
	// ---------- 조회 ----------
	
	// 기사 프로필 조회 (신청 이력이 있는지 확인용)
    FixerProfileDTO selectFixerProfile(String userId);
   

	// 카테고리 전체 목록 (신청 화면 체크박스용)
	List<CategoryDTO> selectCategoryList();

	// 기사가 등록한 자격증 목록 (재신청 시 기존 파일 삭제용)
	List<FixerLicenseDTO> selectLicensesByFixerId(String fixerId);


	// ---------- 등록 / 수정 ----------

	// 신규 신청
	int insertFixerProfile(FixerProfileDTO profile);

	// 재신청 (REJECTED 상태에서 다시 신청)
	int updateFixerProfile(FixerProfileDTO profile);

	// 자격증 1건 등록
	int insertFixerLicense(FixerLicenseDTO license);

	// 활동 지역 1건 등록
	int insertFixerRegion(@Param("fixerId") String fixerId,
						  @Param("regionName") String regionName);

	// 수리 분야 1건 등록
	int insertFixerCategory(@Param("fixerId") String fixerId,
							@Param("categoryId") String categoryId);


	// ---------- 삭제 (재신청 시 기존 데이터 정리) ----------

	int deleteLicensesByFixerId(String fixerId);
	int deleteRegionsByFixerId(String fixerId);
	int deleteCategoriesByFixerId(String fixerId);

}
