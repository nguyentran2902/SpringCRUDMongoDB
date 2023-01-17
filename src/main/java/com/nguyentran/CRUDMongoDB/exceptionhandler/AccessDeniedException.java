package com.nguyentran.CRUDMongoDB.exceptionhandler;

import javax.servlet.http.HttpServletResponse;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessDeniedException  extends RuntimeException{
	
	private String message;
	

}
