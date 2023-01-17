package com.nguyentran.CRUDMongoDB.services;

import java.util.Date;
import java.util.HashMap;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mongodb.client.result.InsertOneResult;
import com.nguyentran.CRUDMongoDB.JWTConfig.CookieUtil;
import com.nguyentran.CRUDMongoDB.entity.Admin;
import com.nguyentran.CRUDMongoDB.exceptionhandler.AccessDeniedException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.DuplicateRecordException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.InternalServerException;
import com.nguyentran.CRUDMongoDB.repositories.AdminRepository;
import com.nguyentran.CRUDMongoDB.repositories.RedisReponsitory;

@Service

public class AdminService {

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private RedisReponsitory redisReponsitory;
	@Autowired
	private PasswordEncoder passwordEncoder;

	static final String JWT_SECRET = "nguyentrannnn";
	static final long JWT_EXPIRATION = 86400000L;
	static final String COOKIE_NAME = "ttr";
//	static final String JWWT_ISSUER = "hahalolo_login";

	// khi người dùng login từ form đăng nhập
	public HashMap<String, String> Login(HttpServletRequest req, HttpServletResponse res, String username,
			String password) {

		// lấy user từ username
		Admin adminExist = adminRepository.loadAdminByUsername(username);
		if (adminExist == null) {

			throw new AccessDeniedException("username không tồn tại");
		}
		try {
			// check password từ user
			Boolean isUser = passwordEncoder.matches(password, adminExist.getPassword());

			if (isUser) {

				// create token
				HashMap<String, String> token = getToken(req, adminExist);
				
				// Lưu token vào redis
				redisReponsitory.saveAccessToken(adminExist.getId().toString(), token.get("access_token"));
				redisReponsitory.saveRefreshToken(adminExist.getId().toString(), token.get("refresh_token"));
				return token;
			} else
				throw new AccessDeniedException("password không đúng!!!");

		} catch (Exception e) {
			throw new InternalServerException(e.getMessage());
		}

	}

	// create token
	private HashMap<String, String> getToken(HttpServletRequest req, Admin admin) {
		Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET.getBytes());
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + JWT_EXPIRATION);

		HashMap<String, String> token = new HashMap<>();
		// create token
		try {
			String access_token = JWT.create().withSubject(admin.getId().toString()).withExpiresAt(expiryDate)
					.withIssuer(req.getRequestURI().toString()).withClaim("roles", admin.getRoles()).sign(algorithm);

			String refresh_token = JWT.create().withSubject(admin.getId().toString())
					.withExpiresAt(new Date(now.getTime() + JWT_EXPIRATION * 3))
					.withIssuer(req.getRequestURI().toString()).withIssuer(req.getRequestURI().toString())
					.sign(algorithm);

			token.put("access_token", access_token);
			token.put("refresh_token", refresh_token);
		} catch (IllegalArgumentException e) {

			throw new InternalServerException(e.getMessage());
		}

		return token;
	}

	// check login từ token client gửi lên
	public boolean isLogin(String token) {

		try {

			Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET.getBytes());
			JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT decodedJWT = verifier.verify(token);
			String idUser = decodedJWT.getSubject();

			if (idUser != null) {
				String accessTokenRedis = redisReponsitory.findItemById("access_token"+idUser);
				String refreshTokenRedis = redisReponsitory.findItemById("refresh_token"+idUser);

				if (accessTokenRedis == null && refreshTokenRedis == null) {
					throw new AccessDeniedException("token hết hạn");
				} 

				if (token.equalsIgnoreCase(accessTokenRedis) || token.equalsIgnoreCase(refreshTokenRedis)) {
					return true;
				}

			}

		} catch (Exception e) {
			throw new InternalServerException("token error: " + e.getMessage());
		}
		return false;
	}

	// get user
	public Admin getUserById(String id) {

		Admin admin = adminRepository.getUserById(id);
		return admin;
	}

	// save User
	public int saveAdmin(Admin admin) {

		Admin adminExist = adminRepository.loadAdminByUsername(admin.getUsername());

		if (adminExist != null) {
			throw new DuplicateRecordException("Username: " + admin.getUsername() + " is exist!!");
		}
		admin.setPassword(passwordEncoder.encode(admin.getPassword()));
		InsertOneResult ior = adminRepository.saveAdmin(admin);
		if (ior.wasAcknowledged()) {
			return 1;

		}
		;

		throw new InternalServerException("Server error");

	}

//	@Override
//	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//		Admin admin = adminRepository.loadAdminByUsername(username);
//		if (admin == null) {
//			throw new UsernameNotFoundException(username + " is not exist!!!");
//		}
//		
//		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//		admin.getRoles().forEach(role -> {
//			authorities.add(new SimpleGrantedAuthority(role));
//		});
//		return new User(admin.getUsername(), admin.getPassword(), authorities);
//	}

}
