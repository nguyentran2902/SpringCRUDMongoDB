package com.nguyentran.CRUDMongoDB.models.CompanyObject;

import lombok.Data;

@Data
public class Branch {
	private int branchType;
	private String address;
	private String phone;
	private int status;

}
