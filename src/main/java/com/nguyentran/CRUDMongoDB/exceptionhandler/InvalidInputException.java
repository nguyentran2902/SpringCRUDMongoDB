package com.nguyentran.CRUDMongoDB.exceptionhandler;

public class InvalidInputException  extends RuntimeException{
	 public InvalidInputException(String message) {
	        super(message);
	    }

}
