package com.fixer.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter @RequiredArgsConstructor
public class SavedFile {
	private final String originalName;  // 자격증.png
	private final String saveName;      // a3f2c1-....png
	private final String path;          // /uploads/license/a3f2c1-....png
}