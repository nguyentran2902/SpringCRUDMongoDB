//package com.nguyentran.CRUDMongoDB.controllers;
//
//import java.io.IOException;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.http.client.ClientProtocolException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import com.nguyentran.CRUDMongoDB.JWTConfig.CookieUtil;
//import com.nguyentran.CRUDMongoDB.common.GooglePojo;
//import com.nguyentran.CRUDMongoDB.common.GoogleUtils;
//import com.nguyentran.CRUDMongoDB.entity.Admin;
//
//@Controller
//@RequestMapping("/admin")
//public class WebController {
//	static final String COOKIE_NAME = "ttr";
////	@Autowired
////	private GoogleUtils googleUtils;
//
//	@GetMapping("/login")
//	public String loginForm(HttpServletRequest request,HttpServletResponse response) {
//		System.out.println(COOKIE_NAME);
//		String authorization = CookieUtil.getCookieValue(request,COOKIE_NAME);
//		System.out.println(authorization);
//		if(authorization!=null) {
//				return "user";
//		} else {
//			 return "login";
//		}
//	}
//	
//	
//	
//	@GetMapping("/logout")
//	public String logOut(HttpServletRequest request,HttpServletResponse response) {
//		CookieUtil.clearCookie(response, COOKIE_NAME);
//		
//		return "login";
//	}
//
////	@RequestMapping("/login-google")
////	public String loginGoogle(HttpServletRequest request) throws ClientProtocolException, IOException {
////		String code = request.getParameter("code");
////
////		if (code == null || code.isEmpty()) {
////			return "redirect:/login?google=error";
////		}
////
////		String accessToken = googleUtils.getToken(code);
////
////		GooglePojo googlePojo = googleUtils.getUserInfo(accessToken);
////		UserDetails userDetail = googleUtils.buildUser(googlePojo);
////		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetail, null,
////				userDetail.getAuthorities());
////		authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
////		SecurityContextHolder.getContext().setAuthentication(authentication);
////		return "redirect:/user";
////	}
//
//	@RequestMapping("/user")
//	public String user() {
//		return "user";
//	}
//
//	@RequestMapping("/admin")
//	public String admin() {
//		return "admin";
//	}
//
//	@RequestMapping("/403")
//	public String accessDenied() {
//		return "403";
//	}
//
//}
