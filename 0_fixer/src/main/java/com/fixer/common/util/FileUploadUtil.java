package com.fixer.common.util;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class FileUploadUtil {

	/**
	 * 업로드된 파일을 서버에 저장하고 결과를 반환
	 *
	 * @param file       업로드된 파일
	 * @param uploadDir  저장할 폴더 (예: uploads/license)
	 * @param webPrefix  웹에서 접근할 주소 (예: /uploads/license)
	 */
	public SavedFile save(MultipartFile file, String uploadDir, String webPrefix)
			throws IllegalStateException, IOException {

		// 1) 파일이 없으면 아무것도 안 함
		if (file == null || file.isEmpty()) {
			return null;
		}

		// 2) 원본 파일명
		String originalName = file.getOriginalFilename();

		// 3) 확장자 추출
		String ext = "";
		int dotIndex = originalName.lastIndexOf(".");
		if (dotIndex > -1) {
			ext = originalName.substring(dotIndex);
		}

		// 4) 겹치지 않는 새 파일명 생성
		String saveName = UUID.randomUUID() + ext;

		// 5) 폴더가 없으면 생성
		File dir = new File(uploadDir).getAbsoluteFile();
		if (!dir.exists()) {
			dir.mkdirs();
		}

		// 6) 실제 저장
		File target = new File(dir, saveName);
		file.transferTo(target);

		// 7) 결과 반환
		String path = webPrefix + "/" + saveName;

		return new SavedFile(originalName, saveName, path);
	}
	
	/**
	 * 저장된 파일을 서버에서 삭제
	 *
	 * @param webPath    DB에 저장된 웹 경로 (/uploads/license/a3f2c1.png)
	 * @param uploadDir  실제 저장 폴더 (uploads/license)
	 */
	public boolean delete(String webPath, String uploadDir) {

		if (webPath == null || webPath.isEmpty()) {
			return false;
		}
		if (uploadDir == null || uploadDir.isEmpty()) {
			return false;
		}

		// 웹 경로에서 파일명만 추출
		String fileName = webPath.substring(webPath.lastIndexOf("/") + 1);

		File target = new File(new File(uploadDir).getAbsoluteFile(), fileName);

		if (target.exists()) {
			target.delete();
		}

		return true;
	}
}