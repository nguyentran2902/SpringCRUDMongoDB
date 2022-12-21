package com.nguyentran.CRUDMongoDB.securityConfig;

import com.nguyentran.CRUDMongoDB.JWTConfig.CustomAuthenticationFilter;
import com.nguyentran.CRUDMongoDB.JWTConfig.CustomAuthorizationFilter;
import com.nguyentran.CRUDMongoDB.services.AdminService;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
//@Order(1)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private AdminService adminService;

//	@Bean
//	public JwtAuthenticationFilter jwtAuthenticationFilter() {
//		return new JwtAuthenticationFilter();
//	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		// Get AuthenticationManager bean
		return super.authenticationManagerBean();
	}

	@Bean
	public static  PasswordEncoder passwordEncoder() {
		// Password encoder, để Spring Security sử dụng mã hóa mật khẩu người dùngs
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(adminService) // Cung cấp userservice cho spring security
				.passwordEncoder(passwordEncoder()); // cung cấp password encoder
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(
				authenticationManagerBean());
		customAuthenticationFilter.setFilterProcessesUrl("/admin/login");

		// vô hiệu hóa Cross Site Request Forgery (mượn quyền trái phép)
		http.csrf().disable();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
		http.authorizeRequests().antMatchers("/admin/login", "/login","/admin/create").permitAll();// Cho phép tất cả mọi người truy cập vào địa chỉ này
		http.authorizeRequests().antMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER");
		http.authorizeRequests().anyRequest().authenticated();

		http.formLogin()
				.and().httpBasic();

		http.addFilter(customAuthenticationFilter);
		http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);

	}

}
