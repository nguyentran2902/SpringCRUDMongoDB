package com.nguyentran.CRUDMongoDB.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.mongodb.client.result.InsertOneResult;
import com.nguyentran.CRUDMongoDB.DTOs.AdminDTO;
import com.nguyentran.CRUDMongoDB.DTOs.PersonDTO;
import com.nguyentran.CRUDMongoDB.common.TourBuzConstants;
import com.nguyentran.CRUDMongoDB.entity.Admin;
import com.nguyentran.CRUDMongoDB.exceptionhandler.AccessDeniedException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.DuplicateRecordException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.ForbiddenException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.InternalServerException;
import com.nguyentran.CRUDMongoDB.repositories.AdminRepository;
import com.nguyentran.CRUDMongoDB.repositories.RedisReponsitory;

import eu.bitwalker.useragentutils.UserAgent;

@Service

public class AdminService
//implements UserDetailsService
{

	@Autowired
	private AdminRepository adminRepository;

	@Autowired
	private RedisReponsitory redisReponsitory;
	@Autowired
	private PasswordEncoder passwordEncoder;

	static final String JWT_SECRET = "nguyentrannnn";
	static final long JWT_EXPIRATION_ACCESSTOKEN = 1500000L; // 1 NGÀY (test 15s)
	static final long JWT_EXPIRATION_REFRESHTOKEN = 2592000000L; // 30 NGÀY

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
				// create new expiration refresh token = 30 day
				Date now = new Date();
				Date newExpirationRefreshToken = new Date(now.getTime() + JWT_EXPIRATION_REFRESHTOKEN);
				String browserName = getDevice(req.getHeader("User-Agent").toUpperCase());
				// create token
				HashMap<String, String> token = getToken(req, browserName, adminExist, newExpirationRefreshToken);
				// get token from redis
				HashMap<String, HashMap<String, Object>> allToken = redisReponsitory
						.findItemById(adminExist.getId().toString());
				if (allToken == null || !allToken.containsKey("access_token")
						|| !allToken.containsKey("refresh_token")) {
					allToken = new HashMap<String, HashMap<String, Object>>();
					HashMap<String, Object> access_token = new HashMap<String, Object>();
					HashMap<String, Object> refresh_token = new HashMap<String, Object>();
					access_token.put(adminExist.getId().toString() + browserName, token.get("access_token"));
					refresh_token.put(adminExist.getId().toString() + browserName, token.get("refresh_token"));
					allToken.put("access_token", access_token);
					allToken.put("refresh_token", refresh_token);
				} else {
					allToken.get("access_token").put(adminExist.getId().toString() + browserName,
							token.get("access_token"));
					allToken.get("refresh_token").put(adminExist.getId().toString() + browserName,
							token.get("refresh_token"));
				}
				redisReponsitory.saveToken(adminExist.getId().toString(), allToken);
				return token;
			} else
				throw new AccessDeniedException("password không đúng!!!");
		} catch (Exception e) {
			throw new InternalServerException(e.getMessage());
		}

	}

	// create token
	private HashMap<String, String> getToken(HttpServletRequest req, String browserName, Admin admin,
			Date expirationRefreshToken) {
		HashMap<String, String> token = new HashMap<>();
		try {
			Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
			Date now = new Date();
			// create token
			String access_token = JWT.create().withSubject(admin.getId().toString())
					// set date expire
					.withExpiresAt(new Date(now.getTime() + JWT_EXPIRATION_ACCESSTOKEN))
					.withIssuer(browserName + " " + req.getRequestURI().toString())
					// lưu role vào claim để author
					.withClaim("roles", admin.getRoles())
					// set chữ ký
					.sign(algorithm);

			String refresh_token = JWT.create().withSubject(admin.getId().toString())
					.withExpiresAt(expirationRefreshToken)
					.withIssuer(browserName + " " + req.getRequestURI().toString())
					// Tạo sự thay đổi khi create refresh token mới
					.withClaim("change", now).sign(algorithm);

			token.put("access_token", access_token);
			token.put("refresh_token", refresh_token);
		} catch (IllegalArgumentException e) {
			throw new InternalServerException(e.getMessage());
		}
		return token;
	}

	// check login từ token client gửi lên
	public boolean checkRoleUser(HttpServletRequest req, String token, String key) {
		try {
			// Giải mã token
			Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
			JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT decodedJWT = verifier.verify(token.trim());

			if (decodedJWT != null) {
				// check access token in redis
				String idUser = decodedJWT.getSubject();
				if (idUser != null) {
					// lấy token từ redis theo id
					String browserName = getDevice(req.getHeader("User-Agent").toUpperCase());
					HashMap<String, HashMap<String, Object>> allToken = redisReponsitory.findItemById(idUser);

					// tất cả token trong redis đã hết hạn => Người dùng cần đăng nhập lại. errCode:
					// 403
					if (allToken == null || allToken.get("access_token") == null) {
						throw new ForbiddenException("access_token was expired");
					}
					Object accessTokenByBrowser = allToken.get("access_token").get(idUser + browserName);
					// token theo browser đó trong redis đã hết hạn => Người dùng cần đăng nhập lại.
					// errCode: 403
					if (accessTokenByBrowser == null || !token.equalsIgnoreCase(accessTokenByBrowser.toString())) {
						throw new ForbiddenException("access_token in this browser was expired");
					}
					// author
					// get Role
					String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
					Boolean isRole = false;
					if (roles != null && roles.length >= 1) {
						switch (key) {
						case TourBuzConstants.BUZ_PAG_PROD_INFE_FNV_VIE_PROD :
							if ( Arrays.stream(roles).anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_MANAGER"))) {
								isRole =  true;
								break;
							}
							break;
						case TourBuzConstants.BUZ_PAG_PROD_FRM_FNC_CRET_UDP_GEN:
							if (Arrays.stream(roles).anyMatch(role ->  role.equals("ROLE_MANAGER"))) {
								isRole = true;
								break;
							}
							break;
						default:
							throw new AccessDeniedException("Your account does not have enough permissions");
						}
						if(isRole) {
							return true;
						} else {
							throw new AccessDeniedException("Your account does not have enough permissions");
						}
					}
				}
			} else {
				throw new ForbiddenException("access_token is not exist");
			}

		} catch (Exception e) {
			throw new ForbiddenException("Token error: " + e.getMessage());
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

		try {
			Admin adminExist = adminRepository.loadAdminByUsername(admin.getUsername());
			if (adminExist != null) {
				throw new DuplicateRecordException("Username: " + admin.getUsername() + " is exist!!");
			}
			admin.setPassword(passwordEncoder.encode(admin.getPassword()));
			InsertOneResult ior = adminRepository.saveAdmin(admin);
			if (ior.wasAcknowledged()) {
				return 1;
			} else {
				return 0;
			}
		} catch (Exception e) {
			throw new InternalServerException(e.getMessage());
		}
	}

	// logout
	public int logout(HttpServletRequest req, HttpServletResponse res, String id) {

		try {
			// get browser
			String browserName = getDevice(req.getHeader("User-Agent").toUpperCase());
			//
			HashMap<String, HashMap<String, Object>> allToken = redisReponsitory.findItemById(id);

			if (allToken != null && allToken.containsKey("access_token") && allToken.get("access_token")!=null
					&& allToken.containsKey("refresh_token") && allToken.get("refresh_token")!=null) {
				allToken.get("access_token").remove(id + browserName);
				allToken.get("refresh_token").remove(id + browserName);
				redisReponsitory.saveToken(id, allToken);
				return 1;
			} else {
				return 2;
			}
		} catch (Exception e) {
			throw new InternalServerException(e.getMessage());
		}
	}

	// get new token from refresh_token
	public HashMap<String, String> refreshtoken(HttpServletRequest req, HttpServletResponse res, String refresh_token) {
		HashMap<String, String> token = new HashMap<>();
		try {
			// Giải mã token
			Algorithm algorithm = Algorithm.HMAC256(JWT_SECRET);
			JWTVerifier verifier = JWT.require(algorithm).build();
			DecodedJWT decodedJWT = verifier.verify(refresh_token.trim());

			System.out.println(decodedJWT.getExpiresAt());
			// get idUser từ token đã mã hóa
			String idUser = decodedJWT.getSubject();

			// Check refresh token
			if (idUser != null) {
				// lấy token từ redis theo id
				String browserName = getDevice(req.getHeader("User-Agent").toUpperCase());
				HashMap<String, HashMap<String, Object>> allToken = redisReponsitory.findItemById(idUser);

				// tất cả token trong redis đã hết hạn => Người dùng cần đăng nhập lại. errCode:
				// 403
				if (allToken.get("refresh_token") == null) {
					throw new ForbiddenException("refresh_token was expired");
				}
				Object refreshTokenByBrowser = allToken.get("refresh_token").get(idUser + browserName);

				// token theo browser đó trong redis đã hết hạn => Người dùng cần đăng nhập lại.
				// errCode: 403
				if (refreshTokenByBrowser == null) {
					throw new ForbiddenException("refresh_token in this browser was expired");
				}

				// Có token trong redis nhưng không khớp với refresh token gửi lên
				// Tức refresh token cũ đã được sử dụng để get 2 token mới và đã đc cập nhật lại
				// trong redis nhưng
				// vẫn có người sử dụng token cũ
				// Nghi vấn có vấn đề bảo mật???
				if (!refresh_token.equalsIgnoreCase(refreshTokenByBrowser.toString())) {
					// Xóa refresh token trong redis
					// Hủy toàn bộ RT đang hoạt động
					// Lúc này cả phiên đăng nhập trước đó sử dụng refresh token cũ mà đã
					// get thành công token mới cũng phải đăng nhập lại
					redisReponsitory.deleteTokenById(idUser);

					throw new ForbiddenException("refresh_token was used");
				}

				// Nếu lọt qua 2 filter trên tức refresh token hợp lệ
				// Cấp mới token

				// Get expriration time của refresh token cũ
				Date exprirationRefreshToken = decodedJWT.getExpiresAt();
				// get new token
				token = getToken(req, browserName, adminRepository.getUserById(idUser), exprirationRefreshToken);

				// Lưu refresh refresh token vào redis
				allToken.get("access_token").put(idUser + browserName, token.get("access_token"));
				allToken.get("refresh_token").put(idUser + browserName, token.get("refresh_token"));
				redisReponsitory.saveToken(idUser, allToken);

			} else {
				throw new ForbiddenException("refresh_token is not exist");
			}
		} catch (Exception e) {
			throw new ForbiddenException("refresh_token error: " + e.getMessage());
		}

		return token;
	}

	public String getDevice(String agenUser) {

		UserAgent userAgent = UserAgent.parseUserAgentString(agenUser);
		String device = userAgent.getBrowser().getName();

		if (agenUser.indexOf("CHROME") != -1 && agenUser.indexOf("EDG") != -1) {
			device = "EDGE";

		} else if (agenUser.indexOf("CHROME") != -1 && agenUser.indexOf("OPR") != -1) {
			device = "OPERA";
		} else if (agenUser.indexOf("POSTMAN") != -1) {
			device = "POSTMAN";
		}
		return device.toUpperCase();
	}

	public HashMap<String, HashMap<String, Object>> getTokenById(String id) {

		return redisReponsitory.findItemById(id);
	}

	public List<AdminDTO> getAllAdmin(int pageNo, int pageSize) {
		List<AdminDTO> admins = adminRepository.getAllPersons(pageNo, pageSize);
		return admins;
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
