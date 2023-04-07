package com.nguyentran.CRUDMongoDB.DTOs;

import java.util.List;

import org.bson.types.ObjectId;

import lombok.Data;

@Data
public class AdminDTO {

	private ObjectId id;
	private String name;
	private String username;
	private List<String> roles;
}
