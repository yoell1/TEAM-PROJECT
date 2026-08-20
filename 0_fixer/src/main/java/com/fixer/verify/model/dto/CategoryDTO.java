package com.fixer.verify.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter @NoArgsConstructor @ToString
public class CategoryDTO {
	private String categoryId;    // CATEGORY_ID   'PC'
	private int    categoryNo;    // CATEGORY_NO   1
	private String categoryItem;  // CATEGORY_ITEM '컴퓨터/노트북'
}