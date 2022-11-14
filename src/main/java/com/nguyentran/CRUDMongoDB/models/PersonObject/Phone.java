package com.nguyentran.CRUDMongoDB.models.PersonObject;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data

public class Phone {
	private String areaCode;
	private String number;
	private String netWork;
	private Integer status;
	
	
	
}
