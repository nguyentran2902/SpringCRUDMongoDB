package com.nguyentran.CRUDMongoDB.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.nguyentran.CRUDMongoDB.entity.Admin;
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
		

		if (admin == null) {
			return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Error: Invalid input data!");
		}

		try {
			int result = adminService.saveAdmin(admin);

			if (result==0) 
				return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("Username is exist!!!");

			if (result==1)
				return ResponseEntity.status(HttpStatus.CREATED).body("save success");

			return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Error: save failed!");

		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
		}
		
	}

}
