package com.fixer.verify.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CategoryDTO {

	/*
CATEGORY_ID		
CATEGORY_NO		
CATEGORY_ITEM
	 */
	private String categoryId;
	private int categoryNo;
	private String categoryItem;
}