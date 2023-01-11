package com.nguyentran.CRUDMongoDB.exceptionhandler;

public class NotFoundException  extends RuntimeException{
	 public NotFoundException(String message) {
	        super(message);
	    }
}
