package com.nguyentran.CRUDMongoDB.models;

import java.util.Date;

import org.springframework.data.annotation.Id;

import com.nguyentran.CRUDMongoDB.models.EmployeeObject.Salary;

import lombok.Data;

@Data
public class Employee {
	@Id
	private String _id;
	private String idPerson;
	private String idCompany;
	private Date timeJoin;
	private Salary salary;
	private int status;
	

}
