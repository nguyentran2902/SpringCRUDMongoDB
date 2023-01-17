package com.nguyentran.CRUDMongoDB.controllers;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import com.nguyentran.CRUDMongoDB.entity.Admin;
import com.nguyentran.CRUDMongoDB.entity.ApiResponse.ApiResponse;
import com.nguyentran.CRUDMongoDB.exceptionhandler.DuplicateRecordException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.InternalServerException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.InvalidInputException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.NoContentException;
import com.nguyentran.CRUDMongoDB.services.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private AdminService adminService;
	
	@PostMapping("/login")
	public ResponseEntity<?> login(HttpServletRequest req, HttpServletResponse res ,
			@RequestParam String username, @RequestParam String password) {
		
		
		if(username.isEmpty() || password.isEmpty()) {
			throw new InvalidInputException("Tài khoản hoặc mật khẩu không được để trống");
		}
		
		try {
			HashMap<String, String> token  = adminService.Login(req,res,username, password);
			if(!token.isEmpty()) {
				ApiResponse apiResponse = new ApiResponse();
				apiResponse.setSuccess(true);
				apiResponse.setCode(200);
				apiResponse.setMessage("success");
				apiResponse.setData(token);
				return ResponseEntity.ok(apiResponse);
			}
			
				throw new NoContentException("");
			} catch (Exception e) {
			throw new InternalServerException(e.getMessage());
		}
		
	
		
	}
	
	@PostMapping("/create")
	public ResponseEntity<?> createAdmin(@RequestBody Admin admin) {
		

		if (admin == null || admin.getName()==null || admin.getPassword()==null) {
			throw new InvalidInputException("invalid input");
		}

		try {
			int result = adminService.saveAdmin(admin);
				
			if (result==1)
				return ResponseEntity.status(HttpStatus.CREATED).body("save success");

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error: save failed!");

		} catch (Exception e) {
			throw new InternalServerException(e.getMessage());
		}
		
	}
	
	
	

}
