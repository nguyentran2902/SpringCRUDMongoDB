package com.nguyentran.CRUDMongoDB.JWTConfig;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.util.WebUtils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
//import com.nguyentran.CRUDMongoDB.models.AdminObject.CustomAdminDetail;
import com.fasterxml.jackson.databind.ObjectMapper;

//@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	static final String JWT_SECRET = "nguyentrannnn";
	static final long JWT_EXPIRATION = 6000000L;
	static final String TOKEN_PREFIX = "Bearer";
	static final String COOKIE_NAME = "ttr";
	private AuthenticationManager authenticationManager;

	public CustomAuthenticationFilter(AuthenticationManager authenticationManager) {
		this.authenticationManager = authenticationManager;
	}

	// lấy username, password để authen
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		String username = request.getParameter("username");
		String password = request.getParameter("password");

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
				password);
		return authenticationManager.authenticate(authenticationToken);
	}

	// sau khi authen thành công thì trả lại token
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {

		User admin = (User) authResult.getPrincipal();

		Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET.getBytes());
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

		// create token
		String access_token = JWT.create()
				.withSubject(admin.getUsername())
				.withExpiresAt(expiryDate)
				.withIssuer(request.getRequestURI().toString())
				.withClaim("roles", admin.getAuthorities().stream()
						.map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algorithm);

		String refresh_token = JWT.create().withSubject(admin.getUsername())
				.withExpiresAt(new Date(now.getTime() + JWT_EXPIRATION * 10))
				.withClaim("roles",
						admin.getAuthorities().stream().map(GrantedAuthority::getAuthority)
								.collect(Collectors.toList()))
				.withIssuer(request.getRequestURI().toString())
				.sign(algorithm);

//		// set cookie
//		Cookie cookie = WebUtils.getCookie(request, COOKIE_NAME);
//		// If exist cookie
//		if (cookie != null) {
//			
//			cookie.setValue(TOKEN_PREFIX+access_token);
//			System.out.println(cookie.getValue());
//			cookie.setMaxAge(-1);
//			cookie.setDomain("localhost");
//			cookie.setPath("/");
//			
//			
//		} else {
		Cookie newCookie = new Cookie(COOKIE_NAME, TOKEN_PREFIX + access_token);
		newCookie.setSecure(false);
		newCookie.setHttpOnly(true);
		newCookie.setMaxAge(-1);
		newCookie.setDomain("localhost");
		newCookie.setPath("/");
		response.addCookie(newCookie);
//		}

		Map<String, String> tokens = new HashMap<>();
		tokens.put("access_token", TOKEN_PREFIX + access_token);
		tokens.put("refresh_token", TOKEN_PREFIX + refresh_token);
		response.setContentType("application/json");
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);

	}

}
