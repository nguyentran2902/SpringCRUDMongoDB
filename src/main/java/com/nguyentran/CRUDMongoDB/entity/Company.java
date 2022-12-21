package com.nguyentran.CRUDMongoDB.entity;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.nguyentran.CRUDMongoDB.entity.CompanyObject.Branch;
import com.nguyentran.CRUDMongoDB.entity.CompanyObject.PaymentCurrency;
import com.nguyentran.CRUDMongoDB.entity.CompanyObject.Purpose;

import lombok.Data;

@Data
@Document(collection = "company")
public class Company {
	@Id
	private String _id;
	private String companyCode;
	private int employeeNumberMax;
	private int employeeNumber;
	private List<Branch> branches;
	private List<PaymentCurrency> paymentCurrencies;
	private List<Purpose> purposes;
	
}
