//package com.nguyentran.CRUDMongoDB.securityConfig;
//
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Lazy;
//import org.springframework.core.annotation.Order;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//
//@Configuration
//@EnableWebSecurity
////@Order(2)
//public class GoogleSecurityConfig extends WebSecurityConfigurerAdapter {
//
//	
//	
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//		// Chỉ cho phép user có quyền ADMIN truy cập đường dẫn /admin/**
//		http.authorizeRequests().antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')");
//		// Chỉ cho phép user có quyền ADMIN hoặc USER truy cập đường dẫn /user/**
//		http.authorizeRequests().antMatchers("/user/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')");
//		// Khi người dùng đã login, với vai trò USER, Nhưng truy cập vào trang yêu cầu
//		// vai trò ADMIN, sẽ chuyển hướng tới trang /403
//		http.authorizeRequests().and().exceptionHandling().accessDeniedPage("/403");
//		// Cấu hình cho Login Form.
//		http.authorizeRequests().and().formLogin()//
//				.loginProcessingUrl("/security_login")//
//				.loginPage("/login")//
//				.defaultSuccessUrl("/user")//
//				.failureUrl("/login?message=error")//
//				.usernameParameter("username")//
//				.passwordParameter("password")
//				// Cấu hình cho Logout Page.
//				.and().logout().logoutUrl("/security_logout").logoutSuccessUrl("/login?message=logout");
//	}
//
//
//}
