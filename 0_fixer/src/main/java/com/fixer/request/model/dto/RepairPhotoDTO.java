package com.fixer.request.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class RepairPhotoDTO {

	private Long    photoId;
	private Long    repairNo;
	private String  photoPath;
	private String  photoType;    // REQUEST(접수사진) / BEFORE / AFTER
	private Integer photoOrder;
}