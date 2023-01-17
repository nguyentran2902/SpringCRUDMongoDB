package com.nguyentran.CRUDMongoDB.entity;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "admin")
public class Admin {
	
	@Id
	private ObjectId id;
	private String name;
	private String username;
	private String password;
	private List<String> roles;
	private int status;
	
	

}
