package com.nguyentran.CRUDMongoDB.entity.CompanyObject;

import lombok.Data;

@Data
public class PaymentCurrency {
	private int type;
	private String paymentCurrency;
}
