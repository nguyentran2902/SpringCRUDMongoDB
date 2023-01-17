package com.nguyentran.CRUDMongoDB.DTOs;

import java.util.List;

import com.nguyentran.CRUDMongoDB.entity.Admin;

import lombok.Data;

@Data
public class AdminDTO {

	private String _id;
	private String name;
	private String username;
	private List<String> roles;
}
