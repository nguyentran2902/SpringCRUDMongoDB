package com.nguyentran.CRUDMongoDB.entity.PersonObject;

import java.io.Serializable;

import org.springframework.data.redis.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data

public class Phone implements Serializable{
	private  String areaCode;
	private @Indexed String number;
	private  String netWork;
	private  Integer status;
	
	
	
}
