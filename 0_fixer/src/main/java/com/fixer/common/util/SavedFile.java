package com.fixer.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SavedFile {

	private final String originalName;	// 원본 파일명 (자격증.png)
	private final String saveName;		// 저장된 파일명 (a3f2c1...png)
	private final String path;			// 웹 접근 경로 (/uploads/license/a3f2c1...png)
}