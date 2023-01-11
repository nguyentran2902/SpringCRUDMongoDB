package com.nguyentran.CRUDMongoDB.JWTConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nguyentran.CRUDMongoDB.exceptionhandler.AccessDeniedException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {

	static final String JWT_SECRET = "nguyentrannnn";
	static final String HEADER_STRING = "authorization";
	static final String TOKEN_PREFIX = "Bearer";
	static final String COOKIE_NAME = "ttr";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		// ko filter với đường dẫn này
		if (request.getServletPath().equals("/admin/login")) {
			filterChain.doFilter(request, response);
		} else {
			// get authorization by request header
//			String authorization = request.getHeader(HEADER_STRING);

			// get authorization by cookie
			String authorization = CookieUtil.getCookieValue(request, COOKIE_NAME);

			// Bắt lấy token và check hợp lệ
			if (authorization != null && authorization.startsWith(TOKEN_PREFIX)) {
				try {
					String token = authorization.substring(TOKEN_PREFIX.length());
					Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET.getBytes());
					JWTVerifier verifier = JWT.require(algorithm).build();
					DecodedJWT decodedJWT = verifier.verify(token);
					String username = decodedJWT.getSubject();
					String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
					Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
					for (String role : roles) {
						authorities.add(new SimpleGrantedAuthority(role));
					}

					UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
							username, null, authorities);
					SecurityContextHolder.getContext().setAuthentication(authenticationToken);

					filterChain.doFilter(request, response);

					// Token ko hợp lệ
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
				log.info("Error logging " + "access denied");
				response.setHeader("error", "access denied");
				response.setStatus(403, "Forbidden");
				Map<String, String> error = new HashMap<>();
				error.put("error_message", "access denied, please login first!!");
				response.setContentType("application/json");
				new ObjectMapper().writeValue(response.getOutputStream(), error);
			}
		}

	}

}
