package com.nguyentran.CRUDMongoDB.exceptionhandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.nguyentran.CRUDMongoDB.entity.ApiResponse.ApiResponse;

@RestControllerAdvice
public class HandleException {

	// not found exception
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<?> handlerNotFoundException(NotFoundException ex) {
		// Log err
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setSuccess(false);
		apiResponse.setCode(400);
		apiResponse.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiResponse);
	}

	// Invalid input exception
	@ExceptionHandler(InvalidInputException.class)
	public ResponseEntity<?> handlerInvalidInputException(InvalidInputException ex) {
		// Log err
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setSuccess(false);
		apiResponse.setCode(400);
		apiResponse.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
	}

	// Duplicate Record exception
	@ExceptionHandler(DuplicateRecordException.class)
	public ResponseEntity<?> handlerDuplicateRecordException(DuplicateRecordException ex) {
		// Log err
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setSuccess(true);
		apiResponse.setCode(400);
		apiResponse.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
	}

	// No Content exception
	@ExceptionHandler(NoContentException.class)
	public ResponseEntity<?> handlerNoContentException(NoContentException ex) {
		// Log err
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setSuccess(true);
		apiResponse.setCode(204);
		apiResponse.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body(apiResponse);
	}

	// Access Denied exception
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<?> handlerAccessDeniedException(AccessDeniedException ex) {
		// Log err
		ex.getRes().setContentType("application/json");
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setSuccess(true);
		apiResponse.setCode(403);
		apiResponse.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(apiResponse);
	}

	// Internal server exception
	@ExceptionHandler(InternalServerException.class)
	public ResponseEntity<?> handlerNoContentException(InternalServerException ex) {
		// Log err
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setSuccess(false);
		apiResponse.setCode(500);
		apiResponse.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
	}

	// Xử lý tất cả các exception chưa được khai báo
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handlerException(Exception ex) {
		// Log err
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setSuccess(false);
		apiResponse.setCode(500);
		apiResponse.setMessage(ex.getMessage());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
	}

}
