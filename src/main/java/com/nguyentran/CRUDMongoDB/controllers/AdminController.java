package com.nguyentran.CRUDMongoDB.controllers;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nguyentran.CRUDMongoDB.DTOs.AdminDTO;
import com.nguyentran.CRUDMongoDB.DTOs.PersonDTO;
import com.nguyentran.CRUDMongoDB.common.TourBuzConstants;
import com.nguyentran.CRUDMongoDB.entity.Admin;
import com.nguyentran.CRUDMongoDB.entity.ApiResponse.ApiResponse;
import com.nguyentran.CRUDMongoDB.exceptionhandler.AccessDeniedException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.DuplicateRecordException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.ForbiddenException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.InternalServerException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.InvalidInputException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.NoContentException;
import com.nguyentran.CRUDMongoDB.services.AdminService;

import eu.bitwalker.useragentutils.UserAgent;

@RestController
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminService;

	
	static final String HEADER_ACCESS_TOKEN	 = "access_token";
	static final String HEADER_REFRESH_TOKEN	 = "refresh_token";
	
	
		// get all person
		@GetMapping("/getAllPerson")
		public ResponseEntity<?> getAllPersons(
				HttpServletRequest request,
				@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
				@RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize) {
			//get token by request header
			String token = request.getHeader(HEADER_ACCESS_TOKEN);		
			if(token == null || token.isEmpty()) {
				throw new AccessDeniedException("Loss token!!!");
			}		
			if(!adminService.checkRoleUser(request,token,TourBuzConstants.BUZ_PAG_PROD_FRM_FNC_CRET_UDP_GEN)) {
				throw new ForbiddenException("You need to login first!!!");
			}	
			try {
				List<AdminDTO> admins = adminService.getAllAdmin(pageNo, pageSize);
				if (admins != null && admins.size() > 0)
					return ResponseEntity.ok(admins);
				return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Not found any person in data");
			} catch (Exception e) {
				return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e);
			}
		}

	// test get token redis by Id
	@GetMapping("/getTokenById")
	public ResponseEntity<?> getTokenById(@RequestParam String id) {
		try {
			HashMap<String, HashMap<String, Object>> map = adminService.getTokenById(id);

			if (map != null) {
				return ResponseEntity.ok(map);
			} else {
				return ResponseEntity.ok("no content");
			}
		} catch (Exception e) {
			throw new InternalServerException(e.getMessage());
		}
	}

	// test get device
	@GetMapping("/getDevice")
	public ResponseEntity<?> getDevice(HttpServletRequest req, HttpServletResponse res) {
//		 get token by request header
		HashMap<String, Object> map = new HashMap<>();
		try {
			String clientIp = req.getRemoteAddr();
			String deviceString = req.getHeader("User-Agent");
			String device = adminService.getDevice(deviceString.toUpperCase());
			map.put("clientIp", clientIp);
			map.put("device", device);
			map.put("deviceString", deviceString);

			return ResponseEntity.ok(map);
		} catch (Exception e) {
			throw new InternalServerException(e.getMessage());
		}
	}

	// login
	@GetMapping("/login")
	public ResponseEntity<?> login(HttpServletRequest req, HttpServletResponse res, @RequestParam String username,
			@RequestParam String password) {

		if (username.isEmpty() || password.isEmpty()) {
			throw new InvalidInputException("Tài khoản hoặc mật khẩu không được để trống");
		}
		try {
			HashMap<String, String> token = adminService.Login(req, res, username, password);
			if (!token.isEmpty()) {
				ApiResponse apiResponse = new ApiResponse();
				apiResponse.setSuccess(true);
				apiResponse.setCode(200);
				apiResponse.setMessage("login success");
				apiResponse.setData(token);
				return ResponseEntity.ok(apiResponse);
			}
			throw new NoContentException("");
		} catch (Exception e) {
			throw new AccessDeniedException(e.getMessage());
		}
	}

	@PostMapping("/create")
	public ResponseEntity<?> createAdmin(@RequestBody Admin admin) {
		if (admin == null || admin.getName() == null || admin.getPassword() == null) {
			throw new InvalidInputException("invalid input");
		}
		HashMap<String, Object> map = new HashMap<>();
		try {
			int result = adminService.saveAdmin(admin);

			if (result == 1) {
				map.put("status", true);
				map.put("message", result);
				map.put("data",admin);

				return ResponseEntity.status(HttpStatus.CREATED).body(map);
			}
			map.put("status", true);
			map.put("message", "failed");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(map);

		} catch (Exception e) {
			throw new InternalServerException(e.getMessage());
		}
	}

	@PostMapping("/refreshtoken")
	public ResponseEntity<?> refreshtoken(HttpServletRequest req, HttpServletResponse res) {

//		 get token by request header
		String refresh_token = req.getHeader(HEADER_REFRESH_TOKEN);

		if (refresh_token == null || refresh_token.isEmpty()) {
			throw new ForbiddenException("Loss token!!!");
		}
		try {
			HashMap<String, String> token = adminService.refreshtoken(req, res, refresh_token);
			if (!token.isEmpty()) {
				ApiResponse apiResponse = new ApiResponse();
				apiResponse.setSuccess(true);
				apiResponse.setCode(200);
				apiResponse.setMessage("success");
				apiResponse.setData(token);
				return ResponseEntity.ok(apiResponse);
			}
			throw new NoContentException("");
		} catch (Exception e) {
			throw new ForbiddenException(e.getMessage());
		}

	}

	@GetMapping("/logout")
	public ResponseEntity<?> logout(HttpServletRequest req, HttpServletResponse res, @RequestParam String id) {

		try {
			int result = adminService.logout(req, res, id);
			if (result == 1) {
				ApiResponse apiResponse = new ApiResponse();
				apiResponse.setSuccess(true);
				apiResponse.setCode(200);
				apiResponse.setMessage("logout success");
				return ResponseEntity.ok(apiResponse);
			} else {
				ApiResponse apiResponse = new ApiResponse();
				apiResponse.setSuccess(true);
				apiResponse.setCode(200);
				apiResponse.setMessage("you was logout!!!");
				return ResponseEntity.ok(apiResponse);
			}
		} catch (Exception e) {
			throw new ForbiddenException(e.getMessage());
		}

	}

}
