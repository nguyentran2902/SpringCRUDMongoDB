package com.nguyentran.CRUDMongoDB.exceptionhandler;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ForbiddenException extends RuntimeException{
	
	private String message;

}
