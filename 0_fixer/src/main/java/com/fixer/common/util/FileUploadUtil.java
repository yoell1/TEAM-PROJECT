package com.fixer.common.util;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadUtil {

	// 자격증 증빙으로 허용할 확장자
	private static final List<String> ALLOWED = List.of(".jpg", ".jpeg", ".png", ".pdf");

	public SavedFile save(MultipartFile file, String uploadDir, String webPrefix)
			throws IOException {

		if (file == null || file.isEmpty()) {
			return null;                       // 안 올린 칸은 조용히 건너뜀
		}

		String originalName = file.getOriginalFilename();
		if (originalName == null) {
			return null;
		}

		// 확장자 추출 + 검사
		String ext = "";
		int dot = originalName.lastIndexOf('.');
		if (dot > -1) {
			ext = originalName.substring(dot).toLowerCase(Locale.ROOT);
		}
		if (!ALLOWED.contains(ext)) {
			throw new IllegalStateException(
					"증빙파일은 jpg, png, pdf 만 올릴 수 있습니다. (" + originalName + ")");
		}

		// 겹치지 않는 새 이름
		String saveName = UUID.randomUUID() + ext;

		File dir = new File(uploadDir).getAbsoluteFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}

		file.transferTo(new File(dir, saveName));

		return new SavedFile(originalName, saveName, webPrefix + "/" + saveName);
	}

	public void delete(String webPath, String uploadDir) {
		if (webPath == null || webPath.isBlank()) {
			return;
		}
		String fileName = webPath.substring(webPath.lastIndexOf('/') + 1);
		File target = new File(new File(uploadDir).getAbsoluteFile(), fileName);
		if (target.exists()) {
			target.delete();
		}
	}
}