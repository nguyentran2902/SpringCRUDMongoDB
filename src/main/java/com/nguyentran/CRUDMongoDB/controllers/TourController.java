package com.nguyentran.CRUDMongoDB.controllers;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nguyentran.CRUDMongoDB.common.TourBuzConstants;
import com.nguyentran.CRUDMongoDB.entity.ApiResponse.ApiResponse;
import com.nguyentran.CRUDMongoDB.entity.ApiResponse.Meta;
import com.nguyentran.CRUDMongoDB.exceptionhandler.AccessDeniedException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.ForbiddenException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.HandleException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.InternalServerException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.InvalidInputException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.NoContentException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.NotFoundException;
import com.nguyentran.CRUDMongoDB.services.AdminService;
import com.nguyentran.CRUDMongoDB.services.TourService;

@RestController
@RequestMapping("/admin/tour")
//@PreAuthorize("hasRole('ROLE_ADMIN')")
public class TourController {

	@Autowired
	private TourService tourService;
	
	@Autowired
	private AdminService adminService;
	
	static final String HEADER_ACCESS_TOKEN	 = "access_token";

	@GetMapping("/getInfosTour")
	public ResponseEntity<?> getInfosTour(
			HttpServletRequest request,
			@RequestParam(value = "numSlot", defaultValue = "1", required = false) String numSlot,
			@RequestParam(value = "lang", required = false) String lang,
			@RequestParam(value = "date", required = false) String date,
			@RequestParam(value = "currency", defaultValue = "vnd", required = false) String currency,
			@RequestParam(value = "pageNo", defaultValue = "1", required = false) int pageNo,
			@RequestParam(value = "pageSize", defaultValue = "6", required = false) int pageSize)
			{
		
		//get token by request header
		String token = request.getHeader(HEADER_ACCESS_TOKEN);		
		if(token == null || token.isEmpty()) {
			throw new AccessDeniedException("Loss token!!!");
		}		
		if(!adminService.checkRoleUser(request,token,TourBuzConstants.BUZ_PAG_PROD_INFE_FNV_VIE_PROD)) {
			throw new ForbiddenException("You need to login first!!!");
		}		
		// check input
		if (numSlot == null  || !isNumeric(numSlot) || Integer.valueOf(numSlot) <= 0) {
			throw new InvalidInputException("Invalid input: numSlot");
		}
		
		try {
			// get infos
			List<Document> infosTour = tourService.getInfosTour(Integer.valueOf(numSlot), lang, date, currency, pageNo, pageSize);		
			ApiResponse apiResponse = new ApiResponse();
			//call APi succsess and get data
			if (infosTour != null && infosTour.size() > 0) {				
				Meta meta= new Meta(infosTour.size(),pageNo);
				apiResponse.setSuccess( true);
				apiResponse.setCode(200);
				apiResponse.setData(infosTour);
				apiResponse.setMeta(meta);
				return ResponseEntity.ok(apiResponse);
			}
			
			//call API success but not found data
			throw new NoContentException("No Content");
			
		} catch (Exception e) {
			throw new InternalServerException(e.getMessage());
		}

	}
	
	public boolean isNumeric(String str) { 
		  try {  
		    Integer.parseInt(str);  
		    return true;
		  } catch(NumberFormatException e){  
		    return false;  
		  }  
		}
	
	

}
