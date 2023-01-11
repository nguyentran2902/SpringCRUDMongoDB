package com.nguyentran.CRUDMongoDB.entity.ApiResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Meta {

	private int totalRecord;
	private int pageNo;

}
