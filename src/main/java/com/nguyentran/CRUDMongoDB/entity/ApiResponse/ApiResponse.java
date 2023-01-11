package com.nguyentran.CRUDMongoDB.entity.ApiResponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class ApiResponse {

	private Boolean success;
	private String message;
	private int code;
	private Object data;
	private Meta meta;
	
	
	
	
}


