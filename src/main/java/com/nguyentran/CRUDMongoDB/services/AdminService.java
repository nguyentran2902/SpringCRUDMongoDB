package com.nguyentran.CRUDMongoDB.services;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.mongodb.client.result.InsertOneResult;
import com.nguyentran.CRUDMongoDB.entity.Admin;
import com.nguyentran.CRUDMongoDB.exceptionhandler.DuplicateRecordException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.InternalServerException;
import com.nguyentran.CRUDMongoDB.exceptionhandler.NotFoundException;
import com.nguyentran.CRUDMongoDB.repositories.AdminRepository;


@Service

public class AdminService implements UserDetailsService {


	@Autowired
	private AdminRepository adminRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Admin admin = adminRepository.loadAdminByUsername(username);
		if (admin == null) {
			throw new UsernameNotFoundException(username + " is not exist!!!");
		}
		
		Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
		admin.getRoles().forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role));
		});
		return new User(admin.getUsername(), admin.getPassword(), authorities);
	}

	// get user
	public Admin getUserById(String id) {
		
		Admin admin = adminRepository.getUserById(id);
		return admin;
	}

	// save User
	public int saveAdmin(Admin admin) {

		Admin adminExist = adminRepository.loadAdminByUsername(admin.getUsername());
		
		if(adminExist!=null) {
			throw new DuplicateRecordException("Username: "+admin.getUsername() +" is exist!!");
		}
		admin.setPassword(passwordEncoder.encode(admin.getPassword()));
		InsertOneResult ior = adminRepository.saveAdmin(admin);
		 if(ior.wasAcknowledged()) {
			 return 1;
		
		 };
		 
		 throw new InternalServerException("Server error");

	}
	
	

}
