package com.nguyentran.CRUDMongoDB.exceptionhandler;

public class DuplicateRecordException extends RuntimeException{

	public DuplicateRecordException(String message) {
		super(message);
	}
}
