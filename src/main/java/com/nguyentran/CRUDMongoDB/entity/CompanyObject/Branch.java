package com.nguyentran.CRUDMongoDB.entity.CompanyObject;

import lombok.Data;

@Data
public class Branch {
	private int branchType;
	private String address;
	private String phone;
	private int status;

}
