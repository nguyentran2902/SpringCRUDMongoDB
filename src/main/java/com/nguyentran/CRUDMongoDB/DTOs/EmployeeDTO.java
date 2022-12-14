package com.nguyentran.CRUDMongoDB.DTOs;

import java.util.Date;

import com.nguyentran.CRUDMongoDB.entity.EmployeeObject.Salary;

import lombok.Data;



@Data
public class EmployeeDTO {
	private String _id;
	private String idPerson;
	private String idCompany;
	private Date timeJoin;
	private Salary salary;
	private int status;
}
