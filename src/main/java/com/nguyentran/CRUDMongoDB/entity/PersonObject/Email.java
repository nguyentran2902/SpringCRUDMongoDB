package com.nguyentran.CRUDMongoDB.entity.PersonObject;

import java.io.Serializable;

import org.springframework.data.redis.core.index.Indexed;

import lombok.*;


@Data

public class Email implements Serializable{
 
	private @Indexed String email;
	private  Integer type;
	
	
}
