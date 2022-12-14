package com.nguyentran.CRUDMongoDB.DTOs;

import java.util.List;

import com.nguyentran.CRUDMongoDB.entity.CompanyObject.Branch;
import com.nguyentran.CRUDMongoDB.entity.CompanyObject.PaymentCurrency;
import com.nguyentran.CRUDMongoDB.entity.CompanyObject.Purpose;

import lombok.Data;

@Data
public class CompanyDTO {
	private String _id;
	private String companyCode;
	private int employeeNumberMax;
	private int employeeNumber;
	private List<Branch> branches;
	private List<PaymentCurrency> paymentCurrencies;
	private List<Purpose> purposes;
}
