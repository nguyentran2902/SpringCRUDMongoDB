package com.nguyentran.CRUDMongoDB.controllers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nguyentran.CRUDMongoDB.JWTConfig.CookieUtil;
import com.nguyentran.CRUDMongoDB.entity.Admin;
import com.nguyentran.CRUDMongoDB.exceptionhandler.DuplicateRecordException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.InternalServerException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.InvalidInputException;
import com.nguyentran.CRUDMongoDB.services.AdminService;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Autowired
	private AdminService adminService;
	
	@PostMapping("/login")
	public void login(@RequestBody Admin admin) {
		
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
