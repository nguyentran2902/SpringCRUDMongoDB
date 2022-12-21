package com.nguyentran.CRUDMongoDB.JWTConfig;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.data.repository.support.Repositories;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
//import com.nguyentran.CRUDMongoDB.models.AdminObject.CustomAdminDetail;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

//@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

	
	private final String JWT_SECRET = "nguyentrannnn";
	private final long JWT_EXPIRATION = 600000L;
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

	//sau khi authen thành công thì trả lại token
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		
		User admin = (User) authResult.getPrincipal();
		
		Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET.getBytes());
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);
		
		

		String access_token = JWT.create()
				.withSubject(admin.getUsername())
				.withExpiresAt(expiryDate)
				.withIssuer(request.getRequestURI().toString())
				.withClaim("roles", admin.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
				.sign(algorithm);
		
		String refresh_token = JWT.create()
				.withSubject(admin.getUsername())
				.withExpiresAt(new Date(now.getTime() + JWT_EXPIRATION*10))
				.withIssuer(request.getRequestURI().toString())
				.sign(algorithm);
		
		
		Map<String, String> tokens = new HashMap<>();
		tokens.put("access_token", access_token);
		tokens.put("refresh_token", refresh_token);
		response.setContentType("application/json");
		new ObjectMapper().writeValue(response.getOutputStream(), tokens);
	}

}
