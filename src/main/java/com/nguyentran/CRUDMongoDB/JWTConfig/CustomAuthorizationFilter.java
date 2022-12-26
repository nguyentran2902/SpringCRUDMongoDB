package com.nguyentran.CRUDMongoDB.JWTConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

	private final String JWT_SECRET = "nguyentrannnn";

	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		//ko filter với đường dẫn này
		if(request.getServletPath().equals("/admin/login")) {
			filterChain.doFilter(request, response);
		} else {
			String authorizationHeader = request.getHeader("authorization");
			
			//Bắt lấy token và check hợp lệ
			if(authorizationHeader !=null && authorizationHeader.startsWith("Bearer ")) {
				try {
					
					
					String token = authorizationHeader.substring("Bearer ".length());
					Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET.getBytes());
					JWTVerifier verifier = JWT.require(algorithm).build();
					DecodedJWT decodedJWT = verifier.verify(token);
					String username = decodedJWT.getSubject();
					String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
					Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
					for (String role :roles) {
						authorities.add(new SimpleGrantedAuthority(role));
					}
					
					UsernamePasswordAuthenticationToken authenticationToken = 
							new UsernamePasswordAuthenticationToken(username,null, authorities);
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);
					filterChain.doFilter(request, response);
					
					//Token ko hợp lệ
				} catch (Exception e) {
					
					log.info("Error logging " + e.getMessage());
					response.setHeader("error", e.getMessage());
					response.setStatus(403, "Forbidden");
					Map<String, String> error = new HashMap<>();
					error.put("error_message", e.getMessage());
					response.setContentType("application/json");
					new ObjectMapper().writeValue(response.getOutputStream(), error);
				
				}
			} else {
				filterChain.doFilter(request, response);
			}
		}
		
	}

}
