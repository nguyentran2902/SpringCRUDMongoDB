package com.nguyentran.CRUDMongoDB.exceptionhandler;

public class InternalServerException  extends RuntimeException{
	 public InternalServerException(String message) {
	        super(message);
	    }

}
