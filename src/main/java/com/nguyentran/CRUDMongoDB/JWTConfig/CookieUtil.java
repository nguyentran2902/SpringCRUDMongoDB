package com.nguyentran.CRUDMongoDB.JWTConfig;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.util.WebUtils;

public class CookieUtil {
	
	public static String getCookieValue(HttpServletRequest request, String name) {
		Cookie cookie = WebUtils.getCookie(request, name);
		 return cookie != null ? cookie.getValue() : null;
	}
	
	public static void clearCookie(HttpServletResponse response, String name) {
		Cookie cookie = new Cookie(name, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
	}
}
