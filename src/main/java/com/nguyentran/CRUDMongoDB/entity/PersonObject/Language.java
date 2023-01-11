package com.nguyentran.CRUDMongoDB.entity.PersonObject;

import java.io.Serializable;

import org.springframework.data.redis.core.index.Indexed;

import lombok.*;


@Data

public class Language implements Serializable{
	private  Integer type;
	private @Indexed String language;
	
	
}
