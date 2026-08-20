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
	List<CategoryDTO> selectCategoryList();

	FixerProfileDTO selectFixerProfile(String fixerId);

	List<FixerLicenseDTO> selectLicensesByFixerId(String fixerId);

	// ---------- 등록 / 수정 ----------
	int insertFixerProfile(FixerProfileDTO profile);   // 신규
	int updateFixerProfile(FixerProfileDTO profile);   // 재신청

	int insertFixerLicense(FixerLicenseDTO license);

	int insertFixerRegion(@Param("fixerId") String fixerId,
	                      @Param("regionName") String regionName);

	int insertFixerCategory(@Param("fixerId") String fixerId,
	                        @Param("categoryId") String categoryId);

	// ---------- 삭제 (재신청 시 기존 정리) ----------
	int deleteLicensesByFixerId(String fixerId);
	int deleteRegionsByFixerId(String fixerId);
	int deleteCategoriesByFixerId(String fixerId);
}